package Entities;

import Users.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Getter
public class UserJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Gender gender;

    @Setter
    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user1_id"),
            inverseJoinColumns = @JoinColumn(name = "user2_id")
    )
    private Set<UserJpaEntity> friends = new HashSet<>();

    private int age;
    private String hairColor;

    public UserJpaEntity() { }
}