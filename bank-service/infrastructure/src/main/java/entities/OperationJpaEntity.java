package entities;

import operations.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operations")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OperationJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "operationtype")
    private OperationType type;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal money;
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountJpaEntity account;
}