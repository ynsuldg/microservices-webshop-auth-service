package se.iths.martin.authserverprojekt2.exception;

/**
 * Signalerar att den efterfrågade användaren saknas.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Skapar UserNotFoundException med de beroenden och inställningar som tjänsten behöver.
     *
     * @param message felmeddelandet
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
