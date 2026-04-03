package Entities;

import Operations.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operations")
@Getter
@AllArgsConstructor
public class OperationJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private OperationType type;

    BigDecimal money;
    LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountJpaEntity account;

    public OperationJpaEntity() { }
}