package br.com.persistenceapi.core.pool;

import br.com.persistenceapi.core.exception.EmptyPoolException;
import br.com.persistenceapi.core.exception.PoolCreationException;
import br.com.persistenceapi.core.exception.PropertiesConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Classe que representa o pool de conexões.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class JDBCConnectionPool {

    /*pool*/
    private static List<Connection> connectionPool;
    
    /*dados do arquivo de propriedades*/
    private static Integer poolSize;
    private static String driver;
    private static String user;
    private static String pass;
    private static String host;
    private static String databaseName;
    private static Integer timeout;
    private static boolean isNeverTimeout;

    /*flag para controle da leitura do arquivo de propriedades*/
    private static boolean isPoolAlreadyConfigured;

    private final String MSG_PROPERTY_CONFIGURATION_EXCEPTION = "Erro ao realizar a configuração do pool.";

    /**
     * Construtor da classe.
     * @throws br.com.persistenceapi.core.exception.PoolCreationException Representa um erro de criação do pool.
     */
    public JDBCConnectionPool() throws PoolCreationException {    
        try {
            if(!isPoolAlreadyConfigured){
                this.initializeConfiguration();
                this.connectionPool = new ArrayList<>(this.poolSize);
                this.initializeConnectionPool();
                if(!isNeverTimeout){
                    new TerminatePoolThread(this).start();
                }
            }
        } catch (PropertiesConfigurationException | SQLException ex) {
            throw new PoolCreationException("Erro na criação do pool.", ex);
        }
    }

    /**
     * Implementação de método responsável por ler todos os dados do arquivo de propriedades.
     * @throws PropertiesConfigurationException Representa um erro na leitura do arquivo de propriedades. Este erro pode ser ocasionado pela ausência do arquivo, nome de arquivo incorreto ou algum erro de I/O no fechamento do arquivo.
     */
    private void initializeConfiguration() throws PropertiesConfigurationException {
        Properties poolProperties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("..//database.properties"));
            poolProperties.load(fis);
            this.validateConfigurations(poolProperties);
        } catch (IOException ioe) {
            throw new PropertiesConfigurationException("Erro ao ler o arquivo de configuração. Arquivo inexistente ou o nome de arquivo de configuração incorreto. O nome deve ser 'database.properties'.", ioe);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                    throw new PropertiesConfigurationException("Erro ao fechar o arquivo de configuração.", ioe);
                }
            }
        }
    }

    /**
     * Realiza a validação dos dados oriundos do arquivo de propriedades.
     * @param poolProperties Representa o arquivo de propriedades.
     * @throws PropertiesConfigurationException Representa um erro na configuração, exemplos: ausência de dados obrigatórios, 
     * dados em formatos incorretos, valores abaixo ou acima do permitido.
     */
    private void validateConfigurations(Properties poolProperties) throws PropertiesConfigurationException{        
        this.isPoolAlreadyConfigured = true;

        String poolSize = poolProperties.getProperty("poolSize");
        if("".equals(poolSize)){
            //valor default
            this.poolSize = 10;
        } else {
            try {
                this.poolSize = Integer.parseInt(poolSize);
            } catch (NumberFormatException nfe) {
                throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " O valor para o tamanho do pool deve ser inteiro.");
            }

            if(this.poolSize > 30 || this.poolSize < 10){
                throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + 
                    " Tamanho do pool acima ou abaixo do permitido. O tamanho do pool deve estar entre 10 e 30, inclusive.");
            }
        }

        this.user = poolProperties.getProperty("user");
        if("".equals(this.user)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " Usuário do banco não especificado. Este campo é obrigatório.");
        }

        this.pass = poolProperties.getProperty("pass");
        if("".equals(this.pass)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " Senha do banco não especificada. Este campo é obrigatório.");
        }

        this.driver = poolProperties.getProperty("driver");
        if("".equals(this.driver)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " Driver do banco não especificado. Este campo é obrigatório.");
        }

        this.host = poolProperties.getProperty("host");
        if("".equals(this.driver)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " Host do banco não especificado. Este campo é obrigatório.");
        }
        
        this.databaseName = poolProperties.getProperty("databaseName");
        if("".equals(this.driver)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " Nome do banco não especificado. Este campo é obrigatório.");
        }
        
        String timeout = poolProperties.getProperty("timeout");
        if("".equals(timeout)){
            //valor default
            this.timeout = 30;
        } else {
            try {
                this.timeout = Integer.parseInt(timeout);
            } catch (NumberFormatException nfe) {
                throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " O valor para o timeout deve ser inteiro.");
            }
        }
                
        String neverTimeout = poolProperties.getProperty("neverTimeout");
        if(!"true".equalsIgnoreCase(neverTimeout) && !"false".equalsIgnoreCase(neverTimeout)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + " O valor para a flag deve ser 'true' ou 'false'.");
        }
        this.isNeverTimeout = Boolean.valueOf(neverTimeout);

        if(!this.isNeverTimeout && (this.timeout > 60 || this.timeout < 10)){
            throw new PropertiesConfigurationException(MSG_PROPERTY_CONFIGURATION_EXCEPTION + 
                " O tempo de timeout acima ou abaixo do permitido. O intervalo de tempo deve estar entre 10 e 60 segundos, inclusive.");
        }
    }

    /**
     * Implementação de método que inicializa as conexões no pool.
     * @throws SQLException Representa um erro da criação da conexão.
     * @throws PropertiesConfigurationException Representa um erro na leitura dos dados do arquivo.
     */
    private void initializeConnectionPool() throws SQLException, PropertiesConfigurationException {
        while (!checkIfConnectionPoolIsFull()) {
            connectionPool.add(createNewConnection());
        }
    }

    /**
     * Implementação de método que verifica se pool de conexões está cheio ou não.
     * @return Retorna true caso o pool esteja cheio. Caso contrário, retorna false.
     */
    protected boolean checkIfConnectionPoolIsFull() {
        return connectionPool.size() == this.poolSize;
    }

    /**
     * Implementação de método responsável por criar uma nova conexão com a base de dados.
     * @return Retorna a conexão criada com a base de dados.
     * @throws SQLException Representa um erro na criação de uma conexão.
     * @throws PropertiesConfigurationException Representa um erro no driver de banco especificado.
     */
    private Connection createNewConnection() throws SQLException, PropertiesConfigurationException {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException cnfe) {
            throw new PropertiesConfigurationException("Erro na leitura do arquivo de configuração. Verifique o valor referente ao driver do banco de dados.");
        }
        return DriverManager.getConnection(this.host + this.databaseName, this.user, this.pass);
    }

    /**
     * Implementação de método sincronizado para a obtenção de conexão.
     * @return Representa a conexão retornada do pool.
     * @throws EmptyPoolException Representa o momento em que o pool não possui conexões disponíveis.
     */
    public synchronized Connection getConnection() throws EmptyPoolException {
        if (connectionPool.size() > 0) {
            Connection connection = connectionPool.get(0);
            connectionPool.remove(0);
            return connection;
        } else {
            throw new EmptyPoolException("O pool não possui conexões disponíveis no momento.");
        }        
    }

    /**
     * Implementação de método que retorna a conexão para o pool.
     * @param connection Representa a conexão.
     */
    public synchronized void returnConnection(Connection connection) {
        connectionPool.add(connection);
    }

    /**
     * Método sobreescrito de Object. Este método encerra todas as conexões caso o objeto do pool seja destruído.
     * @throws Throwable Representa qualquer exceção.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            this.terminateAllConnections();
        } finally {
            super.finalize();
        }
    }

    /**
     * Implementação de método que encerra todas as conexões.
     */
    protected void terminateAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        connectionPool.clear();
        isPoolAlreadyConfigured = false;
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }
}
