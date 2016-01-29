package br.com.persistenceapi.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Preciso Estudar Sempre
 */
public class GenericDAO<T> extends DataSource{

    public void insertUpdateDelete(String sql, List<Object> parametros){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = super.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            this.receiveParameters(preparedStatement, parametros);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            if(connection != null){
                try {
                    connection.rollback();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        } finally {
            this.closeConnection(connection, preparedStatement);
        }
    }
    
    private void receiveParameters(PreparedStatement preparedStatement, List<Object> parametros) throws SQLException{
        int paramPos = 1;
        for(Object parametro : parametros){
            if(parametro == null){
                preparedStatement.setNull(paramPos, 1);
            } else {
                preparedStatement.setObject(paramPos, parametro);
            }
            paramPos++;
        }
    }
    
    public List<T> findAll(String sql, List<Object> parametros, RowMapping rowMapping){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> rows = new ArrayList();
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            this.receiveParameters(preparedStatement, parametros);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                rows.add((T) rowMapping.mapping(resultSet));
            }           
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            if(connection != null){
                try {
                    connection.rollback();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        } finally {
            this.closeConnection(connection, preparedStatement, resultSet);
        }
        return rows;
    }

    public T findById(String sql, List<Object> parametros, RowMapping rowMapping){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        T row = null;
        try {
            connection = this.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            this.receiveParameters(preparedStatement, parametros);
            resultSet = preparedStatement.executeQuery();
            resultSet = resultSet.isBeforeFirst() ? resultSet : null;
            if(resultSet != null){
                resultSet.next();
            }
            row = (T) rowMapping.mapping(resultSet);
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            if(connection != null){
                try {
                    connection.rollback();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        } finally {
            this.closeConnection(connection, preparedStatement, resultSet);
        }
        return row;
    }
}
