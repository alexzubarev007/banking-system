package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;
}