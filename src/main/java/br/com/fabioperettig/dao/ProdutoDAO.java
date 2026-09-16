package br.com.fabioperettig.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.fabioperettig.dao.generic.GenericDAO;
import br.com.fabioperettig.domain.Produto;

public class ProdutoDAO extends GenericDAO<Produto, String> implements IProdutoDAO {
	
    @Override
    public Boolean cadastrar(Produto entity) throws TipoChaveNaoEncontradaException, DAOException {
        return cadastrar(entity, 0);
    }

    @Override
    public Boolean cadastrar(Produto entity, Integer quantidadeInicial) throws DAOException {
        if (quantidadeInicial == null || quantidadeInicial < 0) {
            throw new DAOException("Quantidade inicial inválida", new IllegalArgumentException());
        }
        Long idAnterior = entity.getId();
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stm = conn.prepareStatement(
						getQueryInsercao(), Statement.RETURN_GENERATED_KEYS)
				) {
                    setParametrosQueryInsercao(stm, entity);
                    stm.executeUpdate();
                    try (ResultSet rs = stm.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Produto sem ID.");
                        entity.setId(rs.getLong(1)
						);
                    }
                }

                try (PreparedStatement stm = conn.prepareStatement(
						"""
							INSERT INTO TB_ESTOQUE (ID, ID_PRODUTO_FK, QUANTIDADE)
							VALUES (nextval('sq_estoque'), ?, ?)
							""")) {
                    stm.setLong(1, entity.getId());
                    stm.setInt(2, quantidadeInicial);
                    stm.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                entity.setId(idAnterior);
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("ERRO CADASTRANDO PRODUTO E ESTOQUE", e);
        }
    }

	public ProdutoDAO() {
		super();
	}

	@Override
	public Class<Produto> getTipoClasse() {
		return Produto.class;
	}

	@Override
	public void atualizarDados(Produto entity, Produto entityCadastrado) {
		entityCadastrado.setCupom15Off(entity.getCupom15Off());
		entityCadastrado.setCodigo(entity.getCodigo());
		entityCadastrado.setDescricao(entity.getDescricao());
		entityCadastrado.setNome(entity.getNome());
		entityCadastrado.setValor(entity.getValor());
	}

	@Override
	protected String getQueryInsercao() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO TB_PRODUTO ");
		sb.append("(ID, CODIGO, NOME, DESCRICAO, VALOR, CUPOM_15_OFF)");
		sb.append("VALUES (nextval('sq_produto'),?,?,?,?,?)");
		return sb.toString();
	}

	@Override
	protected void setParametrosQueryInsercao(PreparedStatement stmInsert, Produto entity) throws SQLException {
		stmInsert.setString(1, entity.getCodigo());
		stmInsert.setString(2, entity.getNome());
		stmInsert.setString(3, entity.getDescricao());
		stmInsert.setBigDecimal(4, entity.getValor());
		stmInsert.setBoolean(5, entity.getCupom15Off());
	}

	@Override
	protected String getQueryExclusao() {
		return "DELETE FROM TB_PRODUTO WHERE CODIGO = ?";
	}

	@Override
	protected void setParametrosQueryExclusao(PreparedStatement stmExclusao, String valor) throws SQLException {
		stmExclusao.setString(1, valor);
	}

	@Override
	protected String getQueryAtualizacao() {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE TB_PRODUTO ");
		sb.append("SET CODIGO = ?,");
		sb.append("NOME = ?,");
		sb.append("DESCRICAO = ?,");
		sb.append("VALOR = ?, CUPOM_15_OFF = ?");
		sb.append(" WHERE CODIGO = ?");
		return sb.toString();
	}

	@Override
	protected void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, Produto entity) throws SQLException {
		stmUpdate.setString(1, entity.getCodigo());
		stmUpdate.setString(2, entity.getNome());
		stmUpdate.setString(3, entity.getDescricao());
		stmUpdate.setBigDecimal(4, entity.getValor());
		stmUpdate.setBoolean(5, entity.getCupom15Off());
		stmUpdate.setString(6, entity.getCodigo());
	}

	@Override
	protected void setParametrosQuerySelect(PreparedStatement stmExclusao, String valor) throws SQLException {
		stmExclusao.setString(1, valor);
	}

}
