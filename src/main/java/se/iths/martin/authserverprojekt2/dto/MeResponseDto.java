package se.iths.martin.authserverprojekt2.dto;

import java.util.List;

/**
 * Returnerar den inloggade användarens namn och roller.
 */
public record MeResponseDto(
        String username,
        List<String> roles
) {
}
