package br.com.fabioperettig.dao;

import br.com.fabioperettig.dao.generic.IGenericDAO;
import br.com.fabioperettig.domain.Venda;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;

public interface IVendaDAO extends IGenericDAO<Venda, String> {

	public void finalizarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException;
	public void cancelarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException;
}
