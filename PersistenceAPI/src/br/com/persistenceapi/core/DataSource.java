package br.com.persistenceapi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author joao.maida
 */
public class DataSource {

    private final JDBCConnectionPool pool;

    public DataSource(){
        this.pool = new JDBCConnectionPool();
    }

    public Connection getConnection() throws SQLException  {
        return pool.getConnection();
    }

    public void closeConnection(Connection connection) {
        pool.returnConnection(connection);
    }
    
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
