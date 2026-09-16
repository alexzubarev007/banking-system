package mapping.todomain;

import authentifications.Authentication;
import entities.AuthenticationJpaEntity;

public class AuthenticationToDomainMapping {
    public static Authentication mapToDomain(AuthenticationJpaEntity authentificationEntity) {
        return new Authentication(
                authentificationEntity.getId(),
                authentificationEntity.getLogin(),
                authentificationEntity.getPasswordHash(),
                authentificationEntity.getRole(),
                authentificationEntity.getUser() != null ? authentificationEntity.getUser().getId() : null);
    }
}
