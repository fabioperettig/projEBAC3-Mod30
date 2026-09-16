package br.com.fabioperettig.dao;

import br.com.fabioperettig.exceptions.DAOException;


import br.com.fabioperettig.dao.generic.IGenericDAO;
import br.com.fabioperettig.domain.Produto;

public interface IProdutoDAO extends IGenericDAO<Produto, String>{

    Boolean cadastrar(Produto produto, Integer quantidadeInicial) throws DAOException;
}
