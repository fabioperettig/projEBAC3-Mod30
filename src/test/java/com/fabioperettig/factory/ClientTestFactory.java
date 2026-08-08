package com.fabioperettig.factory;

import com.fabioperettig.domain.Client;

import java.util.List;
import java.util.Random;

public final class ClientTestFactory {

    private static final List<String> NAMES = List.of(
            "Don", "Laura", "Dina",
            "Vlad", "Nancy", "Fabio"
    );

    private ClientTestFactory() {
    }

    public static String randomName() {
        Random random = new Random();
        int index = random.nextInt(NAMES.size());

        return NAMES.get(index);
    }

    public static Client create(String cpf) {
        Client client = new Client();
        String sortName = randomName();
        client.setName(sortName);
        client.setCpf(cpf);
        client.setContact(String.format("%s@mail.com", sortName));

        return client;
    }

}
