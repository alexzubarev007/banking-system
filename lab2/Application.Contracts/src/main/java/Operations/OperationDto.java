package Operations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationDto(UUID id,
                           OperationType type,
                           LocalDateTime time,
                           BigDecimal money,
                           UUID accountId) { }