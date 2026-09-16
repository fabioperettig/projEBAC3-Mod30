package br.com.fabioperettig.dao.factory;

import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.fabioperettig.domain.Produto;

public class ProdutoFactory {

	
	public static Produto convert(ResultSet rs) throws SQLException {
		Produto prod = new Produto();
		prod.setId(rs.getLong("ID_PRODUTO"));
		prod.setCodigo(rs.getString("CODIGO"));
		prod.setNome(rs.getString("NOME"));
		prod.setDescricao(rs.getString("DESCRICAO"));
		prod.setValor(rs.getBigDecimal("VALOR"));

		///NOVO CAMPO
		prod.setCupom15Off(rs.getBoolean("CUPOM_15_OFF"));

		return prod;
	}
}
