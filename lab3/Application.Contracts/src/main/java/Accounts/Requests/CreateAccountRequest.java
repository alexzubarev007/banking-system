package Accounts.Requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CreateAccountRequest(
        @Schema(description = "User id")
        UUID userId) { }
