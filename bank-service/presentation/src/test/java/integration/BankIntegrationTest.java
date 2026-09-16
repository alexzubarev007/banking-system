package integration;

import runners.App;
import services.*;
import accounts.requests.*;
import messages.*;
import brokers.CachedRateProvider;
import rates.CurrencyProblemException;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.junit.jupiter.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@Testcontainers
@SpringBootTest(
        classes = App.class,
        properties = {"spring.rabbitmq.template.reply-timeout=500ms", "rates.cache-ttl=2s"})
@Import(BankIntegrationTest.BrokerFixture.class)
class BankIntegrationTest {
    @Container
    static PostgreSQLContainer pg =
            new PostgreSQLContainer("postgres:15").withInitScript("init.sql");

    @Container static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:4-management");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
        r.add("spring.rabbitmq.host", rabbit::getHost);
        r.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        r.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        r.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @Autowired JdbcTemplate sql;
    @Autowired AccountService service;
    @Autowired BalanceService balances;
    @Autowired CachedRateProvider cache;
    @Autowired RabbitTemplate broker;
    @Autowired PasswordEncoder encoder;
    UUID owner, first, second;
    org.springframework.security.core.userdetails.User principal;

    @BeforeEach
    void seed() {
        sql.execute("TRUNCATE operations, accounts, authentifications, friends, users CASCADE");
        owner = UUID.randomUUID();
        first = UUID.randomUUID();
        second = UUID.randomUUID();
        sql.update(
                "INSERT INTO users(id,name,gender,age,haircolor) VALUES (?,"
                    + " 'Alice','FEMALE',22,'black')",
                owner);
        sql.update(
                "INSERT INTO authentifications(id,login,password_hash,role,user_id) VALUES (?,"
                    + " 'alice', ?, 'CLIENT', ?)",
                UUID.randomUUID(),
                encoder.encode("test-password"),
                owner);
        sql.update(
                "INSERT INTO accounts VALUES (?,100.00,?),(?,0.00,?)", first, owner, second, owner);
        principal =
                new org.springframework.security.core.userdetails.User(
                        "alice", "unused", List.of());
    }

    BigDecimal balance(UUID id) {
        return sql.queryForObject("SELECT balance FROM accounts WHERE id=?", BigDecimal.class, id);
    }

    int history() {
        return sql.queryForObject("SELECT count(*) FROM operations", Integer.class);
    }

    @Test
    void transferPersistsBothBalancesAndHistory() {
        service.transfer(new TransferRequest(first, second, new BigDecimal("25.00")), principal);
        assertEquals(new BigDecimal("75.00"), balance(first));
        assertEquals(new BigDecimal("25.00"), balance(second));
        assertEquals(2, history());
    }

    @Test
    void secondHistoryWriteFailureRollsBackEntireTransfer() {
        sql.execute(
                "CREATE OR REPLACE FUNCTION reject_test_operation() RETURNS trigger LANGUAGE"
                    + " plpgsql AS $$ BEGIN IF NEW.type='PUT' AND NEW.money=7 THEN RAISE EXCEPTION"
                    + " 'test failure'; END IF; RETURN NEW; END $$");
        sql.execute(
                "CREATE TRIGGER reject_test_operation BEFORE INSERT ON operations FOR EACH ROW"
                    + " EXECUTE FUNCTION reject_test_operation()");
        try {
            assertThrows(
                    RuntimeException.class,
                    () ->
                            service.transfer(
                                    new TransferRequest(first, second, new BigDecimal("7.00")),
                                    principal));
            assertEquals(new BigDecimal("100.00"), balance(first));
            assertEquals(new BigDecimal("0.00"), balance(second));
            assertEquals(0, history());
        } finally {
            sql.execute("DROP TRIGGER reject_test_operation ON operations");
            sql.execute("DROP FUNCTION reject_test_operation()");
        }
    }

    @Test
    void concurrentDepositsDoNotLoseUpdates() throws Exception {
        try (var pool = Executors.newFixedThreadPool(8)) {
            var futures = new ArrayList<Future<?>>();
            for (int i = 0; i < 20; i++)
                futures.add(
                        pool.submit(
                                () ->
                                        service.putMoney(
                                                new PutMoneyRequest(first, BigDecimal.ONE),
                                                principal)));
            for (var f : futures) f.get(20, TimeUnit.SECONDS);
        }
        assertEquals(new BigDecimal("120.00"), balance(first));
        assertEquals(20, history());
    }

    @Test
    void oppositeTransfersLockInSameOrder() throws Exception {
        sql.update("UPDATE accounts SET balance=100 WHERE id=?", second);
        try (var pool = Executors.newFixedThreadPool(4)) {
            var tasks = new ArrayList<Future<?>>();
            for (int i = 0; i < 10; i++) {
                boolean forward = i % 2 == 0;
                tasks.add(
                        pool.submit(
                                () ->
                                        service.transfer(
                                                new TransferRequest(
                                                        forward ? first : second,
                                                        forward ? second : first,
                                                        BigDecimal.ONE),
                                                principal)));
            }
            for (var task : tasks) task.get(20, TimeUnit.SECONDS);
        }
        assertEquals(new BigDecimal("100.00"), balance(first));
        assertEquals(new BigDecimal("100.00"), balance(second));
        assertEquals(20, history());
    }

    @Test
    void brokerRequestReplyAndEvents() throws Exception {
        assertEquals(new BigDecimal("2"), cache.getQuote("USD").rate());
        broker.convertAndSend("rates", new RatesMessage("EUR", new BigDecimal("4"), Instant.now()));
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        boolean received = false;
        while (System.nanoTime() < deadline) {
            try {
                if (cache.getQuote("EUR").rate().compareTo(new BigDecimal("4")) == 0) {
                    received = true;
                    break;
                }
            } catch (CurrencyProblemException expected) {
            }
            Thread.sleep(50);
        }
        assertTrue(received, "event must reach the bank listener");
        assertThrows(CurrencyProblemException.class, () -> cache.getQuote("ZZZ"));
    }

    @Test
    void missingSupplierDoesNotBlockRubOperations() {
        assertThrows(CurrencyProblemException.class, () -> cache.getQuote("OFF"));
        service.putMoney(new PutMoneyRequest(first, BigDecimal.ONE), principal);
        assertEquals(new BigDecimal("101.00"), balance(first));
    }

    @TestConfiguration
    static class BrokerFixture {
        @Bean
        ReplyListener replyListener() {
            return new ReplyListener();
        }
    }

    static class ReplyListener {
        @RabbitListener(queues = "rates_request")
        public RatesResponse reply(RequestMessage request) {
            if (request.code().equals("OFF")) return null;
            return request.code().equals("USD")
                    ? new RatesResponse("USD", new BigDecimal("2"), "Ok", Instant.now())
                    : new RatesResponse(request.code(), null, "Unknown currency", null);
        }
    }
}
