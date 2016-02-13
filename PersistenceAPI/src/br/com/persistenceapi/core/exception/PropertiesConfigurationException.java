package br.com.persistenceapi.core.exception;

/**
 * Exceção que representa algum erro de leitura ou de inexistência do arquivo database.properties.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class PropertiesConfigurationException extends Exception {

    /**
     * Creates a new instance of <code>PropertiesConfigurationException</code>
     * without detail message.
     */
    public PropertiesConfigurationException() {
    }

    /**
     * Constructs an instance of <code>PropertiesConfigurationException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PropertiesConfigurationException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>PropertiesConfigurationException</code>
     * with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the exception cause.
     */
    public PropertiesConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
