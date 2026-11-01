package se.iths.martin.authserverprojekt2.dto;

/**
 * Returnerar användarens id och användarnamn utan lösenord.
 */
public record AppUserResponseDto(
        Long id,
        String username
) {
}
