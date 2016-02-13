package br.com.persistenceapi.core.exception;

/**
 *
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class PoolCreationException extends Exception {

    /**
     * Creates a new instance of <code>PoolCreationException</code> without
     * detail message.
     */
    public PoolCreationException() {
    }

    /**
     * Constructs an instance of <code>PoolCreationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PoolCreationException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>PoolCreationException</code> with the
     * specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the exception cause.
     */
    public PoolCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
