package br.com.fabioperettig;

import br.com.fabioperettig.controller.Controller;
import br.com.fabioperettig.dao.ClienteDAO;
import br.com.fabioperettig.dao.EstoqueDAO;
import br.com.fabioperettig.dao.ProdutoDAO;
import br.com.fabioperettig.dao.VendaDAO;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if ("teste".equals(System.getProperty("ambiente"))) {
            System.out.println("Para usar o banco real, remova a opção -Dambiente=teste.");
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            Controller controller = new Controller(
                    new ClienteDAO(), new ProdutoDAO(),
                    new EstoqueDAO(), new VendaDAO(), scanner);
            controller.executar();
        }
    }
}
