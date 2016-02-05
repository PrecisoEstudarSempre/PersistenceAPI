package br.com.persistenceapi.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inteface genérica do mapeamento resultado do banco(result set) - objeto de domínio
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public interface RowMapping<T> {
    
    /**
     * Método que realiza o mapeamento do result set em um objeto de domínio.
     * @param resultSet Objeto do tipo ResultSet. Contém o retorno da query.
     * @return T Retorna um objeto especificado pela generic.
     * @throws SQLException Caso algum erro aconteça quando for acessar os resultados da query.
     */
    T mapping(ResultSet resultSet) throws SQLException;
}
