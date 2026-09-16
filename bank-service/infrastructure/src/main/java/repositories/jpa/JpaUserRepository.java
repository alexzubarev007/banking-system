package repositories.jpa;

import entities.UserJpaEntity;
import users.Gender;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    @EntityGraph(attributePaths = "friends")
    @Query("""
            SELECT user.friends FROM UserJpaEntity user
            WHERE user.id=:id"""
    )
    List<UserJpaEntity> findFriendsById(
            @Param("id") UUID id);

    @EntityGraph(attributePaths = "friends")
    @Query("""
            SELECT user FROM UserJpaEntity user
            WHERE (COALESCE(:hairColor, user.hairColor) = user.hairColor)
            AND (COALESCE(:gender, user.gender ) = user.gender)
            """
    )
    List<UserJpaEntity> findByHairColorAndGender(
            @Param("hairColor") String hairColor,
            @Param("gender") Gender gender);
}
