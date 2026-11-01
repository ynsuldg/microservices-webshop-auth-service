package se.iths.martin.authserverprojekt2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Validerar den förenklade demoåterställningen utan mejl eller identitetskontroll.
 *
 * @param username kontots e-postadress
 * @param newPassword nytt lösenord med minst åtta tecken
 */
public record PasswordResetRequestDto(
        @NotBlank @Email String username,
        @NotBlank @Size(min = 8, max = 72) String newPassword
) { }
