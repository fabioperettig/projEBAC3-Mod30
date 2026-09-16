package br.com.fabioperettig.dao;

import br.com.fabioperettig.dao.generic.IGenericDAO;
import br.com.fabioperettig.domain.Estoque;
import br.com.fabioperettig.exceptions.DAOException;

public interface IEstoqueDAO extends IGenericDAO<Estoque, Long> {
    void adicionar(Long idProduto, Integer quantidade) throws DAOException;
}
