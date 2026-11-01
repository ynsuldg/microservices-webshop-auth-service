package se.iths.martin.authserverprojekt2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.iths.martin.authserverprojekt2.dto.AppUserRequestDto;
import se.iths.martin.authserverprojekt2.dto.AppUserResponseDto;
import se.iths.martin.authserverprojekt2.exception.UserNotFoundException;
import se.iths.martin.authserverprojekt2.mapper.AppUserMapper;
import se.iths.martin.authserverprojekt2.model.AppUser;
import se.iths.martin.authserverprojekt2.repository.AppUserRepository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Hanterar registrering, användaruppgifter, kontoradering och hashade lösenord vid demoåterställning.
 */
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserMapper appUserMapper;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Hämtar alla användare och mappar dom till DTO
     *
     * @return en lista av alla users som AppUserResponseDto
     */
    public List<AppUserResponseDto> findAll() {
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.stream().map(appUserMapper::toDto).toList();
    }

    /**
     * Hämtar en användare med specifikt id
     *
     * @param id användarens id
     * @return användare som en AppUserResponseDto
     */
    public AppUserResponseDto findById(Long id) {
        AppUser appuser = getAppUser(id);
        return appUserMapper.toDto(appuser);
    }

    /**
     * Hittar användare med id eller kastar exception om användare inte finns
     *
     * @param id användarens id
     * @return hämtar en användare med specifikt id
     */
    private AppUser getAppUser(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    /**
     * Skapar en användare med hashat lösenord och role är alltid satt till USER
     *
     * @param appUserRequestDto användarnamn och lösenord för en ny användare
     * @return sparad användare som en AppUserResponseDto
     * @throws ResponseStatusException om användarnamnet redan finns eller lösenordet är för långt för BCrypt
     */
    public AppUserResponseDto create(AppUserRequestDto appUserRequestDto) {
        if (appUserRequestDto.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must not exceed 72 UTF-8 bytes.");
        }
        if (appUserRepository.findByUsername(appUserRequestDto.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered.");
        }
        AppUser appUser = appUserMapper.toEntity(appUserRequestDto);
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setRole("USER");
        AppUser saved = appUserRepository.save(appUser);
        return appUserMapper.toDto(saved);
    }

    /**
     * Sparar ett nytt hashat lösenord för ett befintligt demokonto utan ägarverifiering.
     * Kontots identitet och roll behålls.
     *
     * @param username kontots e-postadress
     * @param newPassword det nya lösenordet
     * @throws ResponseStatusException om lösenordet är ogiltigt eller kontot saknas
     */
    @Transactional
    public void resetPassword(String username, String newPassword) {
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 8 ||
                newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid new password.");
        }
        var user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
        user.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(user);
    }

    /**
     * Kontrollerar lösenordet och raderar den autentiserade användarens konto.
     *
     * @param username användarnamn från verifierad JWT, aldrig från formuläret
     * @param password nuvarande lösenord som ska matcha det sparade lösenordet
     * @throws ResponseStatusException om kontot saknas eller lösenordet är fel
     */
    @Transactional
    public void deleteOwnAccount(String username, String password) {
        var user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid account or password."));
        if (password == null || password.isBlank() ||
                password.getBytes(StandardCharsets.UTF_8).length > 72 ||
                !passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid account or password.");
        }
        appUserRepository.delete(user);
    }

}
