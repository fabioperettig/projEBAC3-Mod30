package projEBAC3.jdbc.dao;

import projEBAC3.factory.ConnectionFactory;
import projEBAC3.jdbc.domain.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO extends AbstractDAO<Cliente> implements InterfaceDAO<Cliente> {

    /// CRUD session
    @Override
    public Integer cadastrar(Cliente cliente) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();

            /// executa schemaCliente.sql em 'Resources'
            executarSchema(connection);

            String sql = getInsert();
            statement = connection.prepareStatement(sql);

            /// futuramente deixar DAO abstract com getEntidade()
            ///returnando cliente ou produto;
            addParametrosInsert(statement, cliente);

            return statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            /// sempre fechar a conexão após execução.
            closeConnection(connection, statement, null);
        }
    }

    @Override
    public Integer atualizar(Cliente cliente) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            String sql = getUpdate();

            statement = connection.prepareStatement(sql);
            addParametrosUpdate(statement, cliente);

            return statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            /// sempre fechar a conexão após execução.
            closeConnection(connection, statement, null);
        }
    }

    @Override
    public Cliente buscar(String codigo) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        Cliente cliente = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            String sql = getSelect();
            statement = connection.prepareStatement(sql);
            addParametrosSelect(statement, codigo);
            result = statement.executeQuery();

            if (result.next()) {
                cliente = new Cliente();
                Long id = result.getLong("ID");
                String nome = result.getString("NOME");
                String cd = result.getString("CODIGO");

                cliente.setId(id);
                cliente.setNome(nome);
                cliente.setCodigo(cd);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            /// sempre fechar a conexão após execução.
            closeConnection(connection, statement, result);
        }
        return cliente;
    }

    @Override
    public List<Cliente> buscarTodos() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Cliente> list = new ArrayList<>();
        Cliente cliente;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            String sql = getSelectAll();
            statement = connection.prepareStatement(sql);
            result = statement.executeQuery();

            while (result.next()) {
                cliente = new Cliente();
                Long id = result.getLong("ID");
                String nome = result.getString("NOME");
                String cd = result.getString("CODIGO");

                cliente.setId(id);
                cliente.setNome(nome);
                cliente.setCodigo(cd);

                list.add(cliente);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            /// sempre fechar a conexão após execução.
            closeConnection(connection, statement, result);
        }
        return list;
    }

    @Override
    public Integer excluir(Cliente cliente) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            String sql = getDelete();
            statement = connection.prepareStatement(sql);
            addParametrosDelete(statement, cliente);
            return statement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            /// sempre fechar a conexão após execução.
            closeConnection(connection, statement, null);
        }
    }

    /// SQL session
    @Override
    protected String getInsert() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO TB_CLIENTE (ID, CODIGO, NOME) ");
        stringBuilder.append("VALUES (nextval('SQ_CLIENTE'),?,?)");

        return stringBuilder.toString();
    }


    @Override
    protected String getUpdate() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE TB_CLIENTE ");
        stringBuilder.append("SET NOME = ?, CODIGO = ? ");
        stringBuilder.append("WHERE ID = ?");

        return stringBuilder.toString();
    }

    @Override
    protected void addParametrosUpdate(PreparedStatement statement, Cliente cliente) throws SQLException {
        statement.setString(1, cliente.getNome());
        statement.setString(2, cliente.getCodigo());
        statement.setLong(3, cliente.getId());
    }

    @Override
    protected void addParametrosInsert(PreparedStatement statement, Cliente cliente) throws SQLException {
        statement.setString(1, cliente.getCodigo());
        statement.setString(2, cliente.getNome());
    }


    @Override
    protected void addParametrosDelete(PreparedStatement statement, Cliente cliente) throws SQLException {
        statement.setString(1, cliente.getCodigo());
    }

    @Override
    protected String getSchema() {
        return "database/schemaCliente.sql";
    }

    @Override
    protected String getTabela() {
        return "TB_CLIENTE";
    }

}
