package se.iths.martin.authserverprojekt2.dto;

import java.util.List;

/**
 * Returnerar JWT, giltighetstid i sekunder, användarnamn och roller.
 */
public record TokenResponseDto(
        String accessToken,
        Long expiresIn,
        String subject,
        List<String> roles
) {
}
