package se.iths.martin.authserverprojekt2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.iths.martin.authserverprojekt2.model.AppUser;

import java.util.Optional;

/**
 * Hanterar databasåtkomst för användare.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    /**
     * Söker en användare via användarnamn.
     *
     * @param username användarnamnet att söka efter
     * @return den matchande posten, eller ett tomt Optional om den saknas
     */
    Optional<AppUser> findByUsername(String username);
}
