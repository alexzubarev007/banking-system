package operations;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationDto(
        @Schema(description = "Operation id")
        UUID id,
        @Schema(description = "Operation type")
        OperationType type,
        @Schema(description = "Operation time")
        LocalDateTime time,
        @Schema(description = "Operation money")
        BigDecimal money,
        @Schema(description = "Operation account id")
        UUID accountId) { }