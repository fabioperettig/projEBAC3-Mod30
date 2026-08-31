package com.fabioperettig.service;

import com.fabioperettig.dao.IGenericDAO;
import com.fabioperettig.domain.Client;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClientService implements IGenericService<Client, Long>{

    private final IGenericDAO<Client, Long> clientDAO;

    public ClientService(IGenericDAO<Client, Long> clientDAO) {
        this.clientDAO = Objects.requireNonNull(clientDAO, "Client DAO is required");
    }

    @Override
    public Client create(Client entity) {
        return clientDAO.create(entity);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientDAO.findById(id);
    }

    @Override
    public List<Client> findAll() {
        return clientDAO.findAll();
    }

    @Override
    public boolean update(Client entity) {
        return false;
    }

    @Override
    public boolean deleteById(Long aLong) {
        return false;
    }
}
