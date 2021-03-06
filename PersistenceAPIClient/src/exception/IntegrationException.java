package exception;

/**
 * Exceção que representa um erro de integração.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class IntegrationException extends Exception {

    /**
     * Creates a new instance of <code>IntegrationException</code> without
     * detail message.
     */
    public IntegrationException() {
    }

    /**
     * Constructs an instance of <code>IntegrationException</code> with the
     * specified cause.
     *
     * @param cause the exception cause
     */
    public IntegrationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs an instance of <code>IntegrationException</code> with the
     * specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the exception cause
     */
    public IntegrationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
