package br.com.fabioperettig.services;

import br.com.fabioperettig.exceptions.DAOException;

import br.com.fabioperettig.dao.IProdutoDAO;
import br.com.fabioperettig.domain.Produto;
import br.com.fabioperettig.services.generic.GenericService;

public class ProdutoService extends GenericService<Produto, String> implements IProdutoService {

	public ProdutoService(IProdutoDAO dao) {
		super(dao);
	}

    @Override
    public Boolean cadastrar(Produto produto, Integer quantidadeInicial) throws DAOException {
        return ((IProdutoDAO) dao).cadastrar(produto, quantidadeInicial);
    }
}
