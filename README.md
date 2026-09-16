![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Projeto Curso EBAC](https://img.shields.io/badge/Projeto--Curso--EBAC-navy?style=for-the-badge)

# ☕ Projeto número 3 — EBAC, módulo 30

Projeto DAO com sistema CRUD em Java JDBC puro e PostgreSQL, baseado no
[projeto original do módulo 30.](https://github.com/digaomilleniun/backend-java-ebac/tree/main/mod30).

## 📋 Implementações pedidas pelo curso:

- NOVO CAMPO CLIENTE: campo Boolean `ativo`, com valor inicial `true`;
- NOVO CAMPO PRODUTO: campo Boolean `cupom15Off` (`cupom_15_off` no banco), inicialmente `false`.
- NOVA TABELA ESTOQUE: `TB_ESTOQUE`, criada e associada ao produto por chave estrangeira, com quantidade não negativa.

> o cupom 15%OFF altera apenas de forma visual o preço do PRODUTO, ao lado do preço original persistido.

## 🕹️ Classe CONTROLLER adicionada

Embora não tenha no projeto do curso, também implementei uma classe de controle com diferentes inputs e menus
CRUDs completos para cada tipo de entidade. Sendo inicializado na classe Main e tudo podenso ser controlado diretamente
pelo terminal.

```bash
public void executar() {
    System.out.println("SISTEMA LIGADO AO BANCO: (DB_URL)");
    while (true) {
        System.out.println("""
                === Sistema de vendas ===
                1 - Clientes
                2 - Produtos
                3 - Estoque
                4 - Vendas
                0 - Sair
                """);
        try {
            switch (texto("Opção")) {
                case "1" -> clienteMenu();
                case "2" -> produtoMenu();
                case "3" -> estoqueMenu();
                case "4" -> vendasMenu();
                case "0" -> {
                    System.out.println("Até logo!");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }

            {...}
        } catch (Exception e) {...}
    }
}
```

----

### Fabio peretti Guimarães | Ebac mod 29 | JUL 2026
