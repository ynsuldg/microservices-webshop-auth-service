package se.iths.martin.authserverprojekt2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Startar autentiseringstjänstens Spring Boot-applikation.
 */
@SpringBootApplication
public class AuthServerProjekt2Application {

    /**
     * Startar applikationen.
     *
     * @param args argument från kommandoraden
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServerProjekt2Application.class, args);
    }

}
