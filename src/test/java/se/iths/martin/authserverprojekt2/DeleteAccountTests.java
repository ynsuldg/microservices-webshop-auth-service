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

class DeleteAccountTests {
    private final AppUserRepository repository = mock(AppUserRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AppUserService service = new AppUserService(mock(AppUserMapper.class), repository, encoder);

    // Raderar exakt den användare som den autentiserade identiteten pekar på.
    @Test
    void deletesAuthenticatedAccountWithCorrectPassword() {
        var user = new AppUser();
        user.setUsername("owner@example.com");
        user.setPassword(encoder.encode("password123"));
        when(repository.findByUsername("owner@example.com")).thenReturn(Optional.of(user));
        service.deleteOwnAccount("owner@example.com", "password123");
        verify(repository).delete(user);
        verify(repository, never()).deleteById(any());
    }

    // Fel lösenord får inte leda till någon radering.
    @Test
    void rejectsWrongPassword() {
        var user = new AppUser();
        user.setPassword(encoder.encode("password123"));
        when(repository.findByUsername("owner@example.com")).thenReturn(Optional.of(user));
        var error = assertThrows(ResponseStatusException.class,
                () -> service.deleteOwnAccount("owner@example.com", "wrong-password"));
        assertEquals(401, error.getStatusCode().value());
        verify(repository, never()).delete(any());
    }

    // Ett redan borttaget konto ger ett kontrollerat svar utan databasändring.
    @Test
    void rejectsMissingAccount() {
        when(repository.findByUsername("missing@example.com")).thenReturn(Optional.empty());
        var error = assertThrows(ResponseStatusException.class,
                () -> service.deleteOwnAccount("missing@example.com", "password123"));
        assertEquals(401, error.getStatusCode().value());
        verify(repository, never()).delete(any());
    }
}
