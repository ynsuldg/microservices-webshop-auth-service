package se.iths.martin.authserverprojekt2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import se.iths.martin.authserverprojekt2.dto.AppUserRequestDto;
import se.iths.martin.authserverprojekt2.dto.AppUserResponseDto;
import se.iths.martin.authserverprojekt2.dto.RegistrationRequestDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.iths.martin.authserverprojekt2.service.AppUserService;

import java.util.List;

/**
 * Exponerar API-anrop för att skapa och hämta användare.
 */
@RestController
@RequestMapping("/appusers")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;

    /**
     * Metoden hämtar alla användare
     *
     * @return Lista av användare
     */
    @GetMapping
    public List<AppUserResponseDto> findAllUsers() {
        return appUserService.findAll();
    }

    /**
     * Hämtar en anvöndare med id
     *
     * @param id Användarens id
     * @return En anvöndare med specifikt id
     */
    @GetMapping("/{id}")
    public AppUserResponseDto findById(@PathVariable Long id) {
        return appUserService.findById(id);
    }

    /**
     * Registrerar ett konto med validerad e-postadress och lösenord.
     *
     * @param request uppgifter för det nya kontot
     * @return Den skapade användarens id och användarnamn, utan lösenord
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserResponseDto create(@Valid @RequestBody RegistrationRequestDto request) {
        return appUserService.create(new AppUserRequestDto(request.username(), request.password()));
    }

    /**
     * Returnerar konflikt även om två registreringar samtidigt använder samma användarnamn.
     *
     * @return HTTP 409 utan databasdetaljer
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> duplicateUsername() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already registered.");
    }
}
