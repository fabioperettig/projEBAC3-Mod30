package br.com.fabioperettig.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import br.com.fabioperettig.dao.generic.GenericDAO;
import br.com.fabioperettig.domain.Estoque;
import br.com.fabioperettig.exceptions.DAOException;

public class EstoqueDAO extends GenericDAO<Estoque, Long> implements IEstoqueDAO {
    public Class<Estoque> getTipoClasse() { return Estoque.class; }
    public void atualizarDados(Estoque entity, Estoque cadastrado) {
        cadastrado.setQuantidade(entity.getQuantidade());
    }
    protected String getQueryInsercao() {
        return """
                INSERT INTO TB_ESTOQUE (ID, ID_PRODUTO_FK, QUANTIDADE)
                VALUES (nextval('sq_estoque'), ?, ?)
                """;
    }
    protected void setParametrosQueryInsercao(PreparedStatement stm, Estoque entity) throws SQLException {
        stm.setLong(1, entity.getIdProduto());
        stm.setInt(2, entity.getQuantidade());
    }
    protected String getQueryExclusao() {
        return "DELETE FROM TB_ESTOQUE WHERE ID_PRODUTO_FK = ?";
    }

    protected void setParametrosQueryExclusao(PreparedStatement stm, Long id) throws SQLException {
        stm.setLong(1, id);
    }

    protected String getQueryAtualizacao() {
        return "UPDATE TB_ESTOQUE SET QUANTIDADE = ? WHERE ID_PRODUTO_FK = ?";
    }

    protected void setParametrosQueryAtualizacao(PreparedStatement stm, Estoque entity) throws SQLException {
        stm.setInt(1, entity.getQuantidade());
        stm.setLong(2, entity.getIdProduto());
    }

    protected void setParametrosQuerySelect(PreparedStatement stm, Long id) throws SQLException {
        stm.setLong(1, id);
    }

    @Override
    public void adicionar(Long idProduto, Integer quantidade) throws DAOException {
        try (Connection conn = getConnection()) {
            movimentar(conn, idProduto, quantidade, false);
        } catch (SQLException e) {
            throw new DAOException("ERRO ADICIONANDO ESTOQUE", e);
        }
    }

    /// Usa a conexão da VENDA para que ESTOQUE e VENDA participem da mesma transação.
    static void movimentar(Connection conn, Long idProduto, Integer quantidade, boolean saida) throws SQLException {

        if (quantidade == null || quantidade <= 0) throw new SQLException("Quantidade deve ser positiva.");
        String sql = saida
            ? "UPDATE TB_ESTOQUE SET QUANTIDADE = QUANTIDADE - ? WHERE ID_PRODUTO_FK = ? AND QUANTIDADE >= ?"
            : "UPDATE TB_ESTOQUE SET QUANTIDADE = QUANTIDADE + ? WHERE ID_PRODUTO_FK = ?";

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, quantidade);
            stm.setLong(2, idProduto);
            if (saida) stm.setInt(3, quantidade);
            if (stm.executeUpdate() != 1) throw new SQLException("Estoque inexistente ou insuficiente.");
        }
    }
}
