package br.com.persistenceapi.core;

import br.com.persistenceapi.core.exception.EmptyPoolException;
import br.com.persistenceapi.core.exception.PoolCreationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe criada com a finalidade de representar um data source. Esta classe constitui uma camada intermediária entre o pool de conexões e o dao genérico.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class DataSource {

    /*instância do pool*/
    private final JDBCConnectionPool pool;

    /**
     * Construtor da classe DataSource. Inicio aqui o pool de conexões.
     * @throws br.com.persistenceapi.core.exception.PoolCreationException
     */
//    public DataSource() throws PoolCreationException{
    public DataSource() {
        this.pool = new JDBCConnectionPool();
    }

    /**
     * Obtenho a conexão disponível
     * @return Representa a conexão.
     * @throws SQLException Representa um erro de conexão a base de dados.
     * @throws br.com.persistenceapi.core.exception.EmptyPoolException
     */
    public Connection getConnection() throws SQLException, EmptyPoolException  {
        return pool.getConnection();
    }

    /**
     * Retorna a conexão ao pool.
     * @param connection Representa a conexão.
     */
    public void closeConnection(Connection connection) {
        pool.returnConnection(connection);
    }
    
    /**
     * Reliza o encerramento do statement atrelado à conexão e devolve a conexão ao pool.
     * @param connection Representa a conexão.
     * @param preparedStatement Representa o statement.
     */
    public void closeConnection(Connection connection, PreparedStatement preparedStatement) {
        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        this.closeConnection(connection);
    }
    
    /**
     * Reliza o encerramento do result set, statement e devolve a conexão ao pool.
     * @param connection Representa a conexão.
     * @param preparedStatement Representa o statement.
     * @param resultSet Representa o result set.
     */
    public void closeConnection(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        this.closeConnection(connection, preparedStatement);
    }
}
