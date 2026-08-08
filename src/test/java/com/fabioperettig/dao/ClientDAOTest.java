package com.fabioperettig.dao;

import com.fabioperettig.domain.Client;
import com.fabioperettig.factory.ClientTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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


    ///Dry
    @Deprecated
    private Client newClient(String cpf) {
        Client client = new Client();
        client.setName("Fabio");
        client.setCpf(cpf);
        client.setContact("fabio@mail.com");

        return client;
    }

}
