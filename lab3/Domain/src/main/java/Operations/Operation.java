package Operations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
public record Operation(UUID id,
                        OperationType type,
                        BigDecimal money,
                        LocalDateTime time,
                        UUID accountId) { }