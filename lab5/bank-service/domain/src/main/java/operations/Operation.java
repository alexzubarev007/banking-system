package operations;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
public record Operation(UUID id,
                        OperationType type,
                        BigDecimal money,
                        LocalDateTime time,
                        UUID accountId) { }