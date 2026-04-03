import Repositories.*;
import TransactionManagers.JpaTransactionManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import TransactionManager.TransactionManager;

public class Main {
    static void main(String[] args) {
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("default")) {
            EntityManager entityManager = factory.createEntityManager();
            TransactionManager transactionManager = new JpaTransactionManager(entityManager);
            AccountRepository accountRepository = new AccountJpaRepository(entityManager);
            UserRepository userRepository = new UserJpaRepository(entityManager);
            OperationRepository operationRepository = new OperationJpaRepository(entityManager);

            AppRunner runner = new AppRunner(
                    transactionManager,
                    accountRepository,
                    userRepository,
                    operationRepository);

            runner.run();
            entityManager.close();
        }
    }
}
