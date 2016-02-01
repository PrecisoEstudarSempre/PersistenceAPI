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
 *
 * @author joao.maida
 */
public class JDBCConnectionPool {

    private static List<Connection> connectionPool;
    private static Integer poolSize;
    private static String driver;
    private static String user;
    private static String pass;
    private static String host;
    private static String databaseName;
    private static Integer timeout;
    private static boolean isPoolAlreadyConfigured;

    public JDBCConnectionPool() {
        try {
            if(!isPoolAlreadyConfigured){
                this.readProperties();
                this.connectionPool = new ArrayList<>(this.poolSize);
                this.initializeConnectionPool();
                new TerminatePoolThread(this).start();
            }
        } catch (PropertiesConfigurationException | SQLException ex) {
            ex.printStackTrace();
        }
    }

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

    private void initializeConnectionPool() throws SQLException, PropertiesConfigurationException {
        while (!checkIfConnectionPoolIsFull()) {
            connectionPool.add(createNewConnection());
        }
    }

    protected boolean checkIfConnectionPoolIsFull() {
        return connectionPool.size() == this.poolSize;
    }

    private Connection createNewConnection() throws SQLException, PropertiesConfigurationException {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException cnfe) {
            throw new PropertiesConfigurationException("Erro na leitura do arquivo de configuração. Verifique o valor referente ao driver do banco de dados.");
        }
        return DriverManager.getConnection(this.host + this.databaseName, this.user, this.pass);
    }

    public synchronized Connection getConnection() {
        Connection connection = null;
        if (connectionPool.size() > 0) {
            connection = connectionPool.get(0);
            connectionPool.remove(0);
        }
        return connection;
    }

    public synchronized void returnConnection(Connection connection) {
        connectionPool.add(connection);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.terminateAllConnections();
        } finally {
            super.finalize();
        }
    }
/*
    private void terminatePool() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {                
                int timeoutCounter = 0;
                while (true) {
                    if (checkIfConnectionPoolIsFull()) {
                        timeoutCounter++;
                        if (getTimeout() == timeoutCounter) {
                            terminateAllConnections();
                            break;
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.run();
    }
*/
    protected void terminateAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }
}
