package mapping.toJpa;

import accounts.Account;
import entities.AccountJpaEntity;
import entities.UserJpaEntity;
import repositories.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountToJpaMapping {
    private final JpaUserRepository jpaUserRepository;

    public AccountJpaEntity mapToJpa(Account domainAccount) {
        UserJpaEntity user = jpaUserRepository.getReferenceById(domainAccount.getUserId());

        return new AccountJpaEntity(
                domainAccount.getId(),
                domainAccount.getBalance(),
                user);
    }
}