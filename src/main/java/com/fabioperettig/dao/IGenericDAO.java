package com.fabioperettig.dao;

import java.util.List;
import java.util.Optional;

public interface IGenericDAO<T, ID> {
    public T create(T entity) throws Exception;
    public Optional<T> findByID(ID id) throws Exception;
    public List<T> findAll() throws Exception;
    public boolean update(T entity) throws Exception;
    public boolean deleteByID(ID id) throws Exception;

}
