package br.com.fabioperettig.services.generic;

import java.io.Serializable;
import java.util.Collection;

import br.com.fabioperettig.dao.Persistente;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;

public interface IGenericService <T extends Persistente, E extends Serializable> {

    public Boolean cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException;
    public void excluir(E valor) throws DAOException;
    public void alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException;
    public T consultar(E valor) throws DAOException;
    public Collection<T> buscarTodos() throws DAOException;

}
