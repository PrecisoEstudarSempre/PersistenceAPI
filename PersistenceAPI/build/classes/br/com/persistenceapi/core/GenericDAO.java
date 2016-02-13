package br.com.persistenceapi.core;

import br.com.persistenceapi.core.exception.EmptyPoolException;
import br.com.persistenceapi.core.exception.EmptyResultSetException;
import br.com.persistenceapi.core.exception.MoreThanOneResultException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe genérica que representa o DAO Genérico.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 * @param <T> Notação genérica da classe
 */
public class GenericDAO<T> extends DataSource{

    /**
     * Implementação de método que é responsável por realizar as operações de escrita (insert, update, delete) no banco de dados.
     * @param sql Representa a string sql.
     * @param parametros Representa a lista de parâmetros da query.
     */
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
        } catch (EmptyPoolException ex) {
            ex.printStackTrace();
        } finally {
            this.closeConnection(connection, preparedStatement);
        }
    }
    
    /**
     * Implementação de método que é responsável por receber os parâmetros, avaliar se algum deles é nulo e configurá-los no statement.
     * @param preparedStatement Representa o statement oriundo da query.
     * @param parametros Representa a lista de parâmetros da query.
     * @throws SQLException
     */
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
    
    /**
     * Implementação de método responsável por realizar operações de leitura no banco de dados (select) as quais, podem retornar vários registros.
     * @param sql Representa a query a ser executada.
     * @param parametros Representa a lista de parâmetros da query.
     * @param rowMapping Representa o mapeamento do resultado da query com os objetos de entidade.
     * @return Retorna uma lista de objetos oriundos da consulta SQL.
     */
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
        } catch (EmptyPoolException ex) {
            ex.printStackTrace();
        } finally {
            this.closeConnection(connection, preparedStatement, resultSet);
        }
        return rows;
    }

    /**
     * Implementação de método responsável por realizar operações de leitura no banco de dados (select) as quais, somente retornam um registro.
     * @param sql Representa a query a ser executada.
     * @param parametros Representa a lista de parâmetros da query.
     * @param rowMapping Representa o mapeamento do resultado da query com os objetos de entidade.
     * @return Retorna o objeto oriundo da consulta SQL.
     * @throws br.com.persistenceapi.core.exception.EmptyResultSetException
     * @throws br.com.persistenceapi.core.exception.MoreThanOneResultException
     * @throws br.com.persistenceapi.core.exception.EmptyPoolException
     * @throws java.sql.SQLException
     */
    public T findById(String sql, List<Object> parametros, RowMapping rowMapping) throws EmptyResultSetException, MoreThanOneResultException, EmptyPoolException, SQLException{
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
            if(!resultSet.isBeforeFirst()){
                throw new EmptyResultSetException("A consulta SQL realizada não possui resultados.");
            }
            resultSet.last();
            if(resultSet.getRow() > 1){
                throw new MoreThanOneResultException("A consulta SQL retorna mais de um resultado.");
            }
            resultSet.first();
            resultSet.next();// avaliar se é necessário
            row = (T) rowMapping.mapping(resultSet);
            connection.commit();
        } catch (SQLException | EmptyPoolException ex) {
            throw ex;
        } finally {
            this.closeConnection(connection, preparedStatement, resultSet);
        }
        return row;
    }
}
