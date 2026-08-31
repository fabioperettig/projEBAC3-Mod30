package com.fabioperettig.service;

import com.fabioperettig.dao.IGenericDAO;
import com.fabioperettig.domain.Client;
import com.fabioperettig.factory.ClientTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class ClientServiceTest {

    private FakeClientDAO clientDAO;
    private ClientService service;

    @BeforeEach
    void setUp(){
        clientDAO = new FakeClientDAO();
        service = new ClientService(clientDAO);
    }

    @Test
    void shouldDelegateClientCreationToDAO() {
        Client client = ClientTestFactory.create("12345678901");
        Client createdClient = service.create(client);

        Assertions.assertSame(client, clientDAO.receivedClient);
        Assertions.assertSame(client, createdClient);
    }

    @Test
    void shouldDelegateFindByIdToDAO() {
        Client expectedClient = ClientTestFactory.create("23456789012");
        clientDAO.clientToFind = expectedClient;

        Optional<Client> result = service.findById(10L);

        Assertions.assertEquals(10L, clientDAO.receivedId);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(expectedClient, result.orElseThrow());
    }

    @Test
    void shouldReturnEmptyWhenClientIsNotFound() {
        Optional<Client> result = service.findById(99L);

        Assertions.assertEquals(99L, clientDAO.receivedId);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldDelegateFindAllToDAO() {
        Client firstClient = ClientTestFactory.create("12345678901");
        Client secondClient = ClientTestFactory.create("23456789012");

        List<Client> expectedClients = List.of(firstClient, secondClient);
        clientDAO.clientsToFind = expectedClients;

        List<Client> result = service.findAll();

        Assertions.assertSame(expectedClients, result);
    }


    private static class FakeClientDAO implements IGenericDAO<Client, Long> {
        private Client receivedClient;
        private Long receivedId;
        private Client clientToFind;
        private List<Client> clientsToFind = List.of();

        @Override
        public Client create(Client entity) {
            receivedClient = entity;
            return entity;
        }

        @Override
        public Optional<Client> findById(Long id) {
            receivedId = id;
            return Optional.ofNullable(clientToFind);
        }

        @Override
        public List<Client> findAll() {
            return clientsToFind;
        }

        @Override
        public boolean update(Client entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteById(Long aLong) {
            throw new UnsupportedOperationException();
        }
    }

}
