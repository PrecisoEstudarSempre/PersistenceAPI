package br.com.persistenceapi.core.exception;

/**
 *
 * @author João Paulo
 */
public class MoreThanOneResultException extends Exception {

    /**
     * Creates a new instance of <code>MoreThanOneResultException</code> without
     * detail message.
     */
    public MoreThanOneResultException() {
    }

    /**
     * Constructs an instance of <code>MoreThanOneResultException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MoreThanOneResultException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>MoreThanOneResultException</code> with
     * the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the exception cause.
     */
    public MoreThanOneResultException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
