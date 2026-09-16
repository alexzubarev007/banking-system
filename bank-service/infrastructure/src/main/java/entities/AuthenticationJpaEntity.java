package entities;

import authentifications.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.util.UUID;

@Entity
@Table(name = "authentifications")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Role role;

    @OneToOne(optional = true)
    @JoinColumn(name = "user_id")
    UserJpaEntity user;
}
