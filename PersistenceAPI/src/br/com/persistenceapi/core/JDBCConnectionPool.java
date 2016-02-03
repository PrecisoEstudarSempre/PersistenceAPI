package br.com.persistenceapi.core;

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
     */
    public JDBCConnectionPool() {
        try {
            if(!isPoolAlreadyConfigured){
                this.readProperties();
                this.connectionPool = new ArrayList<>(this.poolSize);
                this.initializeConnectionPool();
                if(!isNeverTimeout){
                    new TerminatePoolThread(this).start();
                }
            }
        } catch (PropertiesConfigurationException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Implementação de método responsável por ler todos os dados do arquivo de propriedades.
     * @throws PropertiesConfigurationException Representa um erro na leitura do arquivo de propriedades.
     */
    private void readProperties() throws PropertiesConfigurationException {
        Properties poolProperties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("database.properties"));
            poolProperties.load(fis);
            this.poolSize = Integer.parseInt(poolProperties.getProperty("poolSize"));
            this.user = poolProperties.getProperty("user");
            this.pass = poolProperties.getProperty("pass");
            this.driver = poolProperties.getProperty("driver");
            this.host = poolProperties.getProperty("host");
            this.databaseName = poolProperties.getProperty("databaseName");
            this.timeout = Integer.parseInt(poolProperties.getProperty("timeout"));
            this.isPoolAlreadyConfigured = true;
            this.isNeverTimeout = Boolean.valueOf(poolProperties.getProperty("neverTimeout"));
        } catch (IOException ex) {
            throw new PropertiesConfigurationException("Erro ao ler o arquivo de configuração. Arquivo inexistente ou o nome de arquivo de configuração incorreto. O nome deve ser 'database.properties'.");
        } catch (NumberFormatException nfe) {
            throw new PropertiesConfigurationException("Erro na leitura do arquivo de configuração. O valor para o tamanho do pool e timeout devem ser um inteiros.");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    throw new PropertiesConfigurationException("Erro ao fechar o arquivo de configuração.");
                }
            }
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
     */
    public synchronized Connection getConnection() {
        Connection connection = null;
        if (connectionPool.size() > 0) {
            connection = connectionPool.get(0);
            connectionPool.remove(0);
        }
        return connection;
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
