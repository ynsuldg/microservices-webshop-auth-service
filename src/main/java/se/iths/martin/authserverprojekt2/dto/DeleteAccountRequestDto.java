package se.iths.martin.authserverprojekt2.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Bekräftar kontoradering utan att låta klienten ange vilket konto som ska raderas.
 *
 * @param password användarens nuvarande lösenord
 */
public record DeleteAccountRequestDto(@NotBlank String password) { }
