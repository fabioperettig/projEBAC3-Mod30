package br.com.fabioperettig;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/// Seleciona o DB de testes também ao executar uma classe diretamente pela IDE
public class BancoTeste implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        System.setProperty("ambiente", "teste");
    }
}
