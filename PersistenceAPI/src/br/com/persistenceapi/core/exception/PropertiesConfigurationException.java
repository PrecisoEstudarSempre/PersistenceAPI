package br.com.persistenceapi.core.exception;

/**
 *
 * @author joao.maida
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
}
