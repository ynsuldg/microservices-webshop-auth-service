package se.iths.martin.authserverprojekt2.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Innehåller användarnamn och lösenord för registrering eller inloggning.
 */
public record AppUserRequestDto(
        @NotBlank(message = "Username can't be null.")
        String username,
        @NotBlank(message = "Password can't be null.")
        String password
) {
}
