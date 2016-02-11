package br.com.persistenceapi.core.exception;

/**
 * Exceção que sinaliza que a consulta SQL feita não possui resultado.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class EmptyResultSetException extends Exception {

    /**
     * Creates a new instance of <code>EmptyResultSetException</code> without
     * detail message.
     */
    public EmptyResultSetException() {
    }

    /**
     * Constructs an instance of <code>EmptyResultSetException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public EmptyResultSetException(String msg) {
        super(msg);
    }
}
