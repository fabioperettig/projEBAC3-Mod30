package br.com.fabioperettig.dao.generic;

import br.com.fabioperettig.dao.Persistente;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.MaisDeUmRegistroException;
import br.com.fabioperettig.exceptions.TableException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;

import java.io.Serializable;
import java.util.Collection;

/// interface genérica para GenericDAO (Abstract DAO)
public interface IGenericDAO <T extends Persistente, E extends Serializable> {

    public Boolean cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException;
    public void excluir(E valor) throws DAOException;
    public void alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException;
    public T consultar(E valor) throws MaisDeUmRegistroException, TableException, DAOException;
    public Collection<T> buscarTodos() throws DAOException;
}
