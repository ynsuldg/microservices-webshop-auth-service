package se.iths.martin.authserverprojekt2;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import se.iths.martin.authserverprojekt2.mapper.AppUserMapper;
import se.iths.martin.authserverprojekt2.model.AppUser;
import se.iths.martin.authserverprojekt2.repository.AppUserRepository;
import se.iths.martin.authserverprojekt2.service.AppUserService;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetTests {
    private final AppUserRepository repository = mock(AppUserRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AppUserService service = new AppUserService(mock(AppUserMapper.class), repository, encoder);

    // Byter lösenordet utan att ändra användarens id, namn eller roll.
    @Test
    void replacesPasswordHashAndPreservesAccount() {
        var user = new AppUser();
        user.setId(7L);
        user.setUsername("demo@example.com");
        user.setRole("USER");
        user.setPassword(encoder.encode("old-password"));
        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        service.resetPassword(user.getUsername(), "new-password");
        assertTrue(encoder.matches("new-password", user.getPassword()));
        assertFalse(encoder.matches("old-password", user.getPassword()));
        assertNotEquals("new-password", user.getPassword());
        assertEquals(7L, user.getId());
        assertEquals("demo@example.com", user.getUsername());
        assertEquals("USER", user.getRole());
        verify(repository).save(user);
    }

    // Skapar inte ett konto när e-postadressen saknas.
    @Test
    void rejectsUnknownAccount() {
        when(repository.findByUsername("missing@example.com")).thenReturn(Optional.empty());
        var error = assertThrows(ResponseStatusException.class,
                () -> service.resetPassword("missing@example.com", "new-password"));
        assertEquals(404, error.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    // Nekar för korta lösenord och lösenord som överskrider BCrypt-gränsen i byte.
    @Test
    void rejectsInvalidPasswordsBeforeDatabaseAccess() {
        for (String password : new String[] { "short", "å".repeat(40) }) {
            var error = assertThrows(ResponseStatusException.class,
                    () -> service.resetPassword("demo@example.com", password));
            assertEquals(400, error.getStatusCode().value());
        }
        verifyNoInteractions(repository);
    }
}
