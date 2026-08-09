package com.fabioperettig.dao;

import com.fabioperettig.domain.Client;
import com.fabioperettig.factory.ClientTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class ClientDAOTest  extends DaoIntegrationTestSupport{

    private ClientDAO clientDAO;

    @BeforeEach
    void setUp() {
        clientDAO = new ClientDAO(connectionFactory);
    }

    @Test
    void shouldCreateAndPersistClient() {
        Client client = ClientTestFactory.create("12345678901");

        Client createdClient = clientDAO.create(client);
        Client persistedClient = clientDAO.findById(createdClient.getId()).orElseThrow();

        Assertions.assertNotNull(createdClient.getId());
        Assertions.assertEquals(client.getName(), persistedClient.getName());
        Assertions.assertEquals(client.getCpf(), persistedClient.getCpf());
        Assertions.assertEquals(client.getContact(), persistedClient.getContact());

    }

    @Test
    void shouldFindClientById() {
        clientDAO.create(ClientTestFactory.create("12345678901"));
        Client cClient2 = clientDAO.create(ClientTestFactory.create("23456789012"));
        clientDAO.create(ClientTestFactory.create("34567890123"));

        Optional<Client> clientResult = clientDAO.findById(cClient2.getId());
        Assertions.assertTrue(clientResult.isPresent());

        Client foundClient = clientResult.orElseThrow();

        Assertions.assertEquals(cClient2.getId(), foundClient.getId());
        Assertions.assertEquals(cClient2.getName(), foundClient.getName());
        Assertions.assertEquals(cClient2.getCpf(), foundClient.getCpf());
        Assertions.assertEquals(cClient2.getContact(), foundClient.getContact());

    }

    @Test
    void shouldFindAll(){
        Client cClient1 = clientDAO.create(ClientTestFactory.create("12345678901"));
        Client cClient2 = clientDAO.create(ClientTestFactory.create("23456789012"));
        Client cClient3 = clientDAO.create(ClientTestFactory.create("34567890123"));

        List<Client> clientResult = clientDAO.findAll();

        Assertions.assertNotNull(clientResult);
        Assertions.assertEquals(3, clientResult.size());
        Assertions.assertFalse(clientResult.isEmpty());
    }

    @Test
    void shouldUpdate() {
        Client createdClient = clientDAO.create(ClientTestFactory.create("12345678901"));

        String originalCpf = createdClient.getCpf();
        String originalContact = createdClient.getContact();
        String newName = "Updated New Awesome Name";

        Assertions.assertNotEquals(newName, createdClient.getName());

        createdClient.setName(newName);
        boolean updated = clientDAO.update(createdClient);

        Client persistedClient = clientDAO.findById(createdClient.getId()).orElseThrow();

        Assertions.assertTrue(updated);
        Assertions.assertEquals(newName,persistedClient.getName());
        Assertions.assertEquals(originalCpf,persistedClient.getCpf());
        Assertions.assertEquals(originalContact,persistedClient.getContact());
    }

    @Test
    void shouldDeleteById(){
        Client createdClient = clientDAO.create(ClientTestFactory.create("12345678901"));
        Long clientId = createdClient.getId();

        Assertions.assertTrue(clientDAO.findById(clientId).isPresent());

        boolean deleted = clientDAO.deleteById(clientId);
        Optional<Client> deletedClient = clientDAO.findById(clientId);

        Assertions.assertTrue(deleted);
        Assertions.assertTrue(deletedClient.isEmpty());
    }

    /**
     * @deprecated
     * Replaced by {@link com.fabioperettig.factory.ClientTestFactory#create(String)}.
     */
    @Deprecated
    private Client newClient(String cpf) {
        Client client = new Client();
        client.setName("Fabio");
        client.setCpf(cpf);
        client.setContact("fabio@mail.com");

        return client;
    }

}
