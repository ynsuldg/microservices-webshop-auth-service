package se.iths.martin.authserverprojekt2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Validerar uppgifterna för ett nytt konto innan lösenordet hashats.
 *
 * @param username e-postadress som används som användarnamn
 * @param password lösenord med minst åtta tecken
 */
public record RegistrationRequestDto(
        @NotBlank @Email String username,
        @NotBlank @Size(min = 8, max = 72) String password
) { }
