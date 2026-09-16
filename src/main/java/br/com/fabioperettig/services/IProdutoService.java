package br.com.fabioperettig.services;

import br.com.fabioperettig.exceptions.DAOException;

import br.com.fabioperettig.domain.Produto;
import br.com.fabioperettig.services.generic.IGenericService;

public interface IProdutoService extends IGenericService<Produto, String> {

    Boolean cadastrar(Produto produto, Integer quantidadeInicial) throws DAOException;

}
