![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Projeto Curso EBAC](https://img.shields.io/badge/Projeto--Curso--EBAC-navy?style=for-the-badge)

# ☕ Projeto número 3 — EBAC, módulo 30

Projeto DAO com sistema CRUD em Java JDBC puro e PostgreSQL, baseado no
[projeto original do módulo 30.](https://github.com/digaomilleniun/backend-java-ebac/tree/main/mod30).

## Implementações pedidas pelo curso:

- NOVO CAMPO CLIENTE: campo Boolean `ativo`, com valor inicial `true`;
- NOVO CAMPO PRODUTO: campo Boolean `cupom15Off` (`cupom_15_off` no banco), inicialmente `false`.
- NOVA TABELA ESTOQUE: `TB_ESTOQUE`, criada e associada ao produto por chave estrangeira, com quantidade não negativa.

> o cupom 15%OFF altera apenas de forma visual o preço do PRODUTO, ao lado do preço original persistido.

## Estrutura

```text
src/main/java/
├── anotacao/
└── br/com/fabioperettig/
    ├── dao/           # DAOs, generic, JDBC e factories
    ├── domain/        # Cliente, Produto, Venda, ProdutoQuantidade e Estoque
    ├── exceptions/
    └── services/
src/main/resources/database/schema.sql
src/test/java/br/com/fabioperettig/
```

## Como executar

1. Use JDK 17 ou superior e Maven.
2. Crie dois bancos PostgreSQL vazios: aplicação e testes.
3. Configure um `.env` na raiz ou as variáveis de ambiente:

   ```dotenv
   DB_URL=jdbc:postgresql://localhost:5432/vendas
   TEST_DB_URL=jdbc:postgresql://localhost:5432/vendas_teste
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
   ```

4. Execute `mvn test`. Para compilar e empacotar, execute `mvn package`.

As tabelas e sequências são criadas automaticamente na primeira conexão com cada execução.
O script destina-se à instalação em bancos vazios; não migra tabelas antigas.
A aplicação usa `DB_URL`; os testes Maven e os testes DAO executados pela IDE usam `TEST_DB_URL`.
O banco de testes deve ser exclusivo: os testes originais limpam os registros das tabelas ao terminar.
`AllTests` permite executar a suíte pela IDE; o Maven executa cada classe uma vez.

## Executar pelo console

No IntelliJ, abra `src/main/java/br/com/fabioperettig/Main.java` e execute o método
`main` pelo botão ▶. Use a raiz do projeto como diretório de trabalho para carregar o `.env`.

O menu permite cadastrar, listar, alterar e excluir clientes e produtos, repor estoque,
cadastrar vendas, finalizar e cancelar vendas. As operações usam o banco real de `DB_URL`.
A conexão é aberta ao escolher uma operação; abrir o menu não insere dados de exemplo.
Não use `-Dambiente=teste` nessa execução; a Main recusa essa configuração.

## Estoque

```java
produtoDao.cadastrar(produto, 10);             // Produto novo com dez unidades
estoqueDao.adicionar(produto.getId(), 5);     // Entrada de mais cinco unidades
estoqueDao.consultar(produto.getId());        // Saldo persistido
```

O método original `cadastrar(produto)` cria estoque com saldo zero. A sobrecarga com
quantidade inicial também está disponível em `ProdutoService`.
Ao cadastrar uma venda, os itens são persistidos e baixados juntos. Se faltar saldo,
a transação é desfeita. Finalizar não desconta novamente; cancelar devolve os itens
persistidos apenas uma vez, inclusive quando o cancelamento é repetido.
Alterações nos itens de uma venda consultada continuam apenas em memória, conforme a base original.

---
Fabio Peretti Guimarães | EBAC — módulo 30
