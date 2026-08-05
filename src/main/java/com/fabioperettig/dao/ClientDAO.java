package com.fabioperettig.dao;

import com.fabioperettig.domain.Client;

import java.util.List;
import java.util.Optional;

public class ClientDAO extends AbstractDAO<Client> implements IGenericDAO<Client, Long> {


    @Override
    public Client create(Client entity) throws Exception {
        return null;
    }

    @Override
    public Optional<Client> findByID(Long id) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() throws Exception {
        return List.of();
    }

    @Override
    public boolean update(Client entity) throws Exception {
        return false;
    }

    @Override
    public boolean deleteByID(Long id) throws Exception {
        return false;
    }
}
