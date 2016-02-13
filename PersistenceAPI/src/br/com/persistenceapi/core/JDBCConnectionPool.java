package br.com.persistenceapi.core;

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

    /**
     * Construtor da classe.
     * @throws br.com.persistenceapi.core.exception.PoolCreationException
     */
//    public JDBCConnectionPool() throws PoolCreationException {
    public JDBCConnectionPool()  {
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
            //throw new PoolCreationException("Erro na criação do pool.", ex);
        }
    }

    /**
     * Implementação de método responsável por ler todos os dados do arquivo de propriedades.
     * @throws PropertiesConfigurationException Representa um erro na leitura do arquivo de propriedades.
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
     * 
     * @param poolProperties
     * @throws PropertiesConfigurationException 
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
                throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. O valor para o tamanho do pool deve ser inteiro.");
            }
        }

        this.user = poolProperties.getProperty("user");
        if("".equals(this.user)){
            throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. Usuário do banco não especificado. Este campo é obrigatório.");
        }

        this.pass = poolProperties.getProperty("pass");
        if("".equals(this.pass)){
            throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. Senha do banco não especificada. Este campo é obrigatório.");
        }

        this.driver = poolProperties.getProperty("driver");
        if("".equals(this.driver)){
            throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. Driver do banco não especificado. Este campo é obrigatório.");
        }

        this.host = poolProperties.getProperty("host");
        if("".equals(this.driver)){
            throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. Host do banco não especificado. Este campo é obrigatório.");
        }
        
        this.databaseName = poolProperties.getProperty("databaseName");
        if("".equals(this.driver)){
            throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. Nome do banco não especificado. Este campo é obrigatório.");
        }
        
        String timeout = poolProperties.getProperty("timeout");
        if("".equals(timeout)){
            //valor default
            this.timeout = 30;
        } else {
            try {
                this.timeout = Integer.parseInt(timeout);
            } catch (NumberFormatException nfe) {
                throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. O valor para o timeout deve ser inteiro.");
            }
        }
                
        String neverTimeout = poolProperties.getProperty("neverTimeout");
        if(!"true".equalsIgnoreCase(neverTimeout) && !"false".equalsIgnoreCase(neverTimeout)){
            throw new PropertiesConfigurationException("Erro ao realizar a configuração do pool. O valor para a flag deve ser 'true' ou 'false'.");
        }
        this.isNeverTimeout = Boolean.valueOf(neverTimeout);
    }

    /**
     * Implementação de método que inicializa as conexões no pool.
     * @throws SQLException Representa um erro da criação da conexão.
     * @throws PropertiesConfigurationException Representa um erro na leitura dos dados do arquivo.
     */
    private void initializeConnectionPool() throws SQLException, PropertiesConfigurationException {
        if(!this.isNeverTimeout && this.timeout > 60){
            throw new PropertiesConfigurationException("Erro na inicialização do pool. O tempo de timeout está acima do permitido.");
        }
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
     * @throws PropertiesConfigurationException Representa um erro na leitura dos dados do arquivo.
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
