package br.com.persistenceapi.core.exception;

/**
 * Exceção que sinaliza que o pool está sem conexões disponíveis no momento.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class EmptyPoolException extends Exception {

    /**
     * Creates a new instance of <code>EmptyPoolException</code> without detail
     * message.
     */
    public EmptyPoolException() {
    }

    /**
     * Constructs an instance of <code>EmptyPoolException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public EmptyPoolException(String msg) {
        super(msg);
    }
}
