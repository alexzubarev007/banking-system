import Accounts.AccountDto;
import Accounts.Requests.CreateAccountRequest;
import Accounts.Requests.PutMoneyRequest;
import Accounts.Requests.TransferRequest;
import Accounts.Requests.WithdrawRequest;
import Operations.OperationDto;
import Operations.Requests.GetHistoryRequest;
import Repositories.AccountRepository;
import Repositories.OperationRepository;
import Repositories.UserRepository;
import Services.AccountService;
import Services.OperationService;
import Services.UserService;
import TransactionManager.TransactionManager;
import Users.Gender;
import Users.Requests.AddFriendRequest;
import Users.Requests.CreateUserRequest;
import Users.Requests.GetUserDetailsRequest;
import Users.UserDto;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@AllArgsConstructor
public class AppRunner {
    private TransactionManager transactionManager;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private OperationRepository operationRepository;

    public void run() {
        AccountService accountService = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        UserService userService = new UserService(
                transactionManager,
                userRepository
        );

        OperationService operationService = new OperationService(operationRepository);
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("Hello, this is bank system!");
        while (isRunning) {
            System.out.println("""
                    Choose:
                    1.Create user
                    2.Create account
                    3.Put money
                    4.Withdraw
                    5.Transfer
                    6.Get user details
                    7.Add friends
                    8.Get account operation history
                    9.Exit
                    """);
            int command = scanner.nextInt();

            switch (command) {
                case 1 -> createUser(scanner, userService);
                case 2 -> createAccount(scanner, accountService);
                case 3 -> putMoney(scanner, accountService);
                case 4 -> withdraw(scanner, accountService);
                case 5 -> transfer(scanner, accountService);
                case 6 -> getUserDetails(scanner, userService);
                case 7 -> addFriends(scanner, userService);
                case 8 -> getAccountOperationHistory(scanner, operationService);
                case 9 -> isRunning = false;
            }
        }
    }

    private void createUser(Scanner scanner, UserService userService) {
        System.out.println("Name: ");
        String name = scanner.next();

        System.out.println("Gender(M/F): ");
        String stringGender = scanner.next();

        if (!stringGender.equals("M") && !stringGender.equals("F")) {
            System.out.println("Incorrect gender input");
            return;
        }
        Gender gender = (stringGender.equals("M")) ? Gender.MALE : Gender.FEMALE;

        System.out.println("Age: ");
        int age = scanner.nextInt();

        System.out.println("Hair color: ");
        String hairColor = scanner.next();

        CreateUserRequest request = new CreateUserRequest(name, age, gender, hairColor);

        UserDto user = userService.createUser(request);

        System.out.println("User created!!! ");
        System.out.println("id: " + user.id());
        System.out.println("name: " + user.name());
        System.out.println("age: " + user.age());
        System.out.println("gender: " + user.gender().toString());
        System.out.println("hair color: " + user.hairColor());
    }

    private void createAccount(Scanner scanner, AccountService accountService) {
        System.out.println("User id: ");
        UUID userId = UUID.fromString(scanner.next());

        CreateAccountRequest request = new CreateAccountRequest(userId);

        AccountDto account = accountService.createAccount(request);

        System.out.println("Account created!!! ");
        System.out.println("id: " + account.id());
        System.out.println("user id: " + account.userId());
    }

    private void putMoney(Scanner scanner, AccountService accountService) {
        System.out.println("Account id: ");
        UUID accountId = UUID.fromString(scanner.next());

        System.out.println("Money: ");
        BigDecimal money = new BigDecimal(scanner.next());

        PutMoneyRequest request = new PutMoneyRequest(accountId, money);

        AccountDto account = accountService.putMoney(request);

        System.out.println("Money put!!!");
        System.out.println("account id: " + account.id());
        System.out.println("balance: " + account.balance());
    }

    private void withdraw(Scanner scanner, AccountService accountService) {
        System.out.println("Account id: ");
        UUID accountId = UUID.fromString(scanner.next());

        System.out.println("Money: ");
        BigDecimal money = new BigDecimal(scanner.next());

        WithdrawRequest request = new WithdrawRequest(accountId, money);

        AccountDto account = accountService.withdraw(request);

        System.out.println("Money withdrew!!");
        System.out.println("account id: " + account.id());
        System.out.println("balance: " + account.balance());
    }

    private void transfer(Scanner scanner, AccountService accountService) {
        System.out.println("Account(sender) id: ");
        UUID senderId = UUID.fromString(scanner.next());

        System.out.println("Account(recipient) id: ");
        UUID recipientId = UUID.fromString(scanner.next());

        System.out.println("Money: ");
        BigDecimal money = new BigDecimal(scanner.next());

        TransferRequest request = new TransferRequest(senderId, recipientId, money);

        accountService.transfer(request);
        System.out.println("Money transferred!!");
    }

    private void getUserDetails(Scanner scanner, UserService userService) {
        System.out.println("User id: ");
        UUID userId = UUID.fromString(scanner.next());

        GetUserDetailsRequest request = new GetUserDetailsRequest(userId);

        UserDto user = userService.getUserDetails(request);

        System.out.println("id: " + user.id());
        System.out.println("name: " + user.name());
        System.out.println("age: " + user.age());
        System.out.println("hair color: " + user.hairColor());
        System.out.println("gender: " + user.gender().toString());
        System.out.println("friends:");
        for (UUID friendId : user.friends()) {
            System.out.println(friendId);
        }
    }

    private void addFriends(Scanner scanner, UserService userService) {
        System.out.println("User1 id: ");
        UUID user1Id = UUID.fromString(scanner.next());

        System.out.println("User2 id: ");
        UUID user2Id = UUID.fromString(scanner.next());

        AddFriendRequest request = new AddFriendRequest(user1Id, user2Id);

        userService.addFriend(request);

        System.out.println("Made friends!!!");
    }

    private void getAccountOperationHistory(Scanner scanner, OperationService operationService) {
        System.out.println("Account id: ");
        UUID accountId = UUID.fromString(scanner.next());

        GetHistoryRequest request = new GetHistoryRequest(accountId);

        List<OperationDto> operations = operationService.getHistory(request);

        for (OperationDto operation : operations) {
            System.out.println("id: " + operation.id());
            System.out.println("type: " + operation.type());
            System.out.println("time: " + operation.time());
            System.out.println("money: " + operation.money());
            System.out.println(" ");
        }
    }
}