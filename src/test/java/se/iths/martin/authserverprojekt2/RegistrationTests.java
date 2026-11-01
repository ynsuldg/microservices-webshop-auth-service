package se.iths.martin.authserverprojekt2;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import se.iths.martin.authserverprojekt2.dto.AppUserRequestDto;
import se.iths.martin.authserverprojekt2.dto.AppUserResponseDto;
import se.iths.martin.authserverprojekt2.mapper.AppUserMapper;
import se.iths.martin.authserverprojekt2.model.AppUser;
import se.iths.martin.authserverprojekt2.repository.AppUserRepository;
import se.iths.martin.authserverprojekt2.service.AppUserService;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationTests {
    // Kontrollerar att registrering hashar lösenordet och alltid ger rollen USER.
    @Test
    void createsUserWithHashedPasswordAndUserRole() {
        var repository = mock(AppUserRepository.class);
        var mapper = mock(AppUserMapper.class);
        var encoder = new BCryptPasswordEncoder();
        var request = new AppUserRequestDto("new@example.com", "password123");
        var entity = new AppUser();
        entity.setUsername(request.username());
        entity.setPassword(request.password());
        when(repository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(new AppUserResponseDto(1L, request.username()));
        var response = new AppUserService(mapper, repository, encoder).create(request);
        assertEquals("USER", entity.getRole());
        assertNotEquals(request.password(), entity.getPassword());
        assertTrue(encoder.matches(request.password(), entity.getPassword()));
        assertEquals(request.username(), response.username());
    }

    // Nekar dubletter innan något nytt konto sparas.
    @Test
    void rejectsExistingUsername() {
        var repository = mock(AppUserRepository.class);
        when(repository.findByUsername("used@example.com")).thenReturn(Optional.of(new AppUser()));
        var service = new AppUserService(mock(AppUserMapper.class), repository, new BCryptPasswordEncoder());
        var error = assertThrows(ResponseStatusException.class,
                () -> service.create(new AppUserRequestDto("used@example.com", "password123")));
        assertEquals(409, error.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    // Validerar BCrypt-gränsen i byte även när lösenordet innehåller Unicode.
    @Test
    void rejectsPasswordExceedingBcryptByteLimit() {
        var repository = mock(AppUserRepository.class);
        var service = new AppUserService(mock(AppUserMapper.class), repository, new BCryptPasswordEncoder());
        var error = assertThrows(ResponseStatusException.class,
                () -> service.create(new AppUserRequestDto("new@example.com", "å".repeat(40))));
        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(repository);
    }
}
