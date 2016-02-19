package dao;

import br.com.persistenceapi.core.dao.RowMapping;
import br.com.persistenceapi.core.dao.GenericDAO;
import br.com.persistenceapi.core.exception.EmptyPoolException;
import br.com.persistenceapi.core.exception.EmptyResultSetException;
import br.com.persistenceapi.core.exception.MoreThanOneResultException;
import entidade.Funcionario;
import exception.BusinessException;
import exception.IntegrationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe cliente DAO da entidade Funcionario.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class FuncionarioDAO extends GenericDAO<Funcionario>{

    public void insert(Funcionario funcionario) throws IntegrationException, BusinessException{
        String sql = "INSERT INTO funcionario ("
                + "NM_FUNCIONARIO, "
                + "EM_FUNCIONARIO,"
                + "DT_NASCIMENTO_FUNCIONARIO,"
                + "MAT_FUNCIONARIO,"
                + "NM_LOGRADOURO,"
                + "NUM_LOGRADOURO,"
                + "NM_BAIRRO"
                + ") VALUES (?,?,?,?,?,?,?)";
        List<Object> parametros = new ArrayList<>();
        parametros.add(funcionario.getNome());
        parametros.add(funcionario.getEmail());
        parametros.add(funcionario.getDataNascimento());
        parametros.add(funcionario.getMatricula());
        parametros.add(funcionario.getLogradouro());
        parametros.add(funcionario.getNumero());
        parametros.add(funcionario.getBairro());
        try {
            super.insertUpdateDelete(sql, parametros);
        } catch (EmptyPoolException ex) {
            throw new IntegrationException("Erro de integração com a base de dados.", ex);
        } catch (SQLException ex) {
            throw new BusinessException("Verifique a operação SQL.", ex);
        }
    }
    
    public void update(Funcionario funcionario) throws IntegrationException, BusinessException{
        String sql = "UPDATE FUNCIONARIO SET "
                + "NM_FUNCIONARIO = ?, "
                + "EM_FUNCIONARIO = ?,"
                + "DT_NASCIMENTO_FUNCIONARIO = ?,"
                + "MAT_FUNCIONARIO = ?,"
                + "NM_LOGRADOURO = ?,"
                + "NUM_LOGRADOURO = ?,"
                + "NM_BAIRRO = ? "
            + "WHERE ID = ?";
        List<Object> parametros = new ArrayList<>();
        parametros.add(funcionario.getNome());
        parametros.add(funcionario.getEmail());
        parametros.add(funcionario.getDataNascimento());
        parametros.add(funcionario.getMatricula());
        parametros.add(funcionario.getLogradouro());
        parametros.add(funcionario.getNumero());
        parametros.add(funcionario.getBairro());
        parametros.add(funcionario.getId());
        try {
            super.insertUpdateDelete(sql, parametros);
        } catch (EmptyPoolException ex) {
            throw new IntegrationException("Erro de integração com a base de dados.", ex);
        } catch (SQLException ex) {
            throw new BusinessException("Verifique a operação SQL.", ex);
        }
    }
    
    public void delete(Long id) throws IntegrationException, BusinessException{
        String sql = "DELETE FROM FUNCIONARIO WHERE ID = ?";
        List<Object> parametros = new ArrayList<>();
        parametros.add(id);
        try {
            super.insertUpdateDelete(sql, parametros);
        } catch (EmptyPoolException ex) {
            throw new IntegrationException("Erro de integração com a base de dados.", ex);  
        } catch (SQLException ex) {
            throw new BusinessException("Verifique a operação SQL.", ex);
        }
    }
    
    public List<Funcionario> findAll() throws IntegrationException, BusinessException{
        String sql = "SELECT * FROM FUNCIONARIO";
        List<Object> parametros = new ArrayList<>();
        try{
            return super.findAll(sql, parametros, new RowMapping<Funcionario>() {
                @Override
                public Funcionario mapping(ResultSet resultSet) throws SQLException{
                    Funcionario funcionario = new Funcionario();
                    if(resultSet != null){
                        funcionario.setId(resultSet.getLong("ID"));
                        funcionario.setNome(resultSet.getString("NM_FUNCIONARIO"));
                        funcionario.setEmail(resultSet.getString("EM_FUNCIONARIO"));
                        funcionario.setDataNascimento(resultSet.getDate("DT_NASCIMENTO_FUNCIONARIO"));
                        funcionario.setMatricula(resultSet.getString("MAT_FUNCIONARIO"));
                        funcionario.setLogradouro(resultSet.getString("NM_LOGRADOURO"));
                        funcionario.setNumero(resultSet.getInt("NUM_LOGRADOURO"));
                        funcionario.setBairro(resultSet.getString("NM_BAIRRO"));
                    }
                    return funcionario;
                }
            });
        } catch (EmptyPoolException | SQLException ex) {
            throw new IntegrationException("Erro de integração com a base de dados.", ex);
        } catch (EmptyResultSetException ex) {
            throw new BusinessException(ex);
        }
    }
    
    public Funcionario findById(Long id) throws IntegrationException, BusinessException{
        String sql = "SELECT * FROM FUNCIONARIO WHERE ID = ?";
        List<Object> parametros = new ArrayList<>();
        parametros.add(id);
        try {
            return super.findById(sql, parametros, new RowMapping<Funcionario>() {
                @Override
                public Funcionario mapping(ResultSet resultSet) throws SQLException{
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId(resultSet.getLong("ID"));
                    funcionario.setNome(resultSet.getString("NM_FUNCIONARIO"));
                    funcionario.setEmail(resultSet.getString("EM_FUNCIONARIO"));
                    funcionario.setDataNascimento(resultSet.getDate("DT_NASCIMENTO_FUNCIONARIO"));
                    funcionario.setMatricula(resultSet.getString("MAT_FUNCIONARIO"));
                    funcionario.setLogradouro(resultSet.getString("NM_LOGRADOURO"));
                    funcionario.setNumero(resultSet.getInt("NUM_LOGRADOURO"));
                    funcionario.setBairro(resultSet.getString("NM_BAIRRO"));
                    return funcionario;
                }
            });
        } catch (EmptyResultSetException | MoreThanOneResultException ex) {
            throw new BusinessException("A consulta realizada possui uma não conformidade de negócio.", ex);
        } catch (EmptyPoolException | SQLException ex) {
            throw new IntegrationException("Erro de integração com a base de dados.", ex);
        }
    }
}
