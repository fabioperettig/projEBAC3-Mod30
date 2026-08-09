# projEBAC3 - Módulo 30

Projeto de estudo desenvolvido em Java 17 para praticar persistência com JDBC e
PostgreSQL, sem Spring, JPA, Hibernate ou outro ORM.

O projeto está sendo reconstruído a partir de uma implementação JDBC antiga,
com o objetivo de separar responsabilidades, reduzir repetição nos DAOs e
preparar uma arquitetura em camadas.

## Estado atual

Este checkpoint conclui o CRUD de clientes e produtos e inicia a expansão do
domínio para vendas e estoque:

- entidades `Client` e `Product`;
- schemas PostgreSQL para clientes e produtos;
- contrato genérico de CRUD;
- `AbstractDAO` com o ciclo JDBC compartilhado;
- `ClientDAO` e `ProductDAO` com SQL e mapeamentos específicos;
- `ConnectionFactory` que entrega novas conexões;
- `DatabaseConfig` para carregar credenciais de desenvolvimento e teste;
- `SchemaInitializer` para preparar as tabelas antes do uso;
- `DataAccessException` para falhas de persistência;
- dependência DotEnv e arquivo `.env` local ignorado pelo Git;
- factories para criação dos dados utilizados nos testes;
- testes CRUD de integração para `ClientDAO` e `ProductDAO`;
- `SaleStatus`, com os estados da venda;
- `SaleItem`, com quantidade, preço histórico e subtotal calculado;
- `Sale`, responsável por itens, total e transições de estado.

O ambiente automatizado valida a conexão com o PostgreSQL de testes, inicializa
as tabelas e limpa os dados antes e depois de cada teste. Os testes dos DAOs
cobrem criação, busca por ID, listagem, atualização e exclusão.

## Tecnologias

- Java 17;
- Maven;
- JDBC;
- PostgreSQL;
- DotEnv;
- JUnit 6.

## Arquitetura planejada

```text
Main / Controller
       ↓
    Service
       ↓
 IGenericDAO<T, ID>
       ↓
 AbstractDAO<T, ID>
       ↓
ClientDAO / ProductDAO
       ↓
ConnectionFactory
       ↓
   PostgreSQL
```

O `AbstractDAO` implementa o algoritmo comum de `create`, `findById`,
`findAll`, `update` e `deleteById`. Os DAOs concretos fornecem somente:

- comandos SQL;
- associação dos parâmetros do `PreparedStatement`;
- conversão de uma linha do `ResultSet` em uma entidade;
- leitura e atribuição do ID gerado.

Uma explicação detalhada dessa classe está em
[GUIA_ABSTRACT_DAO.md](GUIA_ABSTRACT_DAO.md).

## Entidades

### Client

- `id`;
- `name`;
- `cpf`;
- `contact`.

O CPF possui restrição de unicidade no banco.

### Product

- `id`;
- `name`;
- `code`;
- `price`, representado por `BigDecimal`;
- `stock`;
- disponibilidade calculada por `isInStock()`.

O código do produto possui restrição de unicidade. Preço e estoque não podem
ser negativos segundo as constraints do schema.

O saldo ainda está armazenado em `Product`, mas será extraído para a entidade
`Stock` na próxima etapa, mantendo uma única fonte de verdade para o estoque.

### Sale

- `id` e código único;
- cliente previamente persistido;
- data da venda;
- status `INITIATED`, `COMPLETED` ou `CANCELLED`;
- coleção de itens indexada pelo ID do produto;
- quantidade total e valor total calculados a partir dos itens.

A venda só pode ser modificada enquanto estiver iniciada e não pode ser
concluída sem itens. Produtos repetidos incrementam a quantidade do item
existente em vez de criar duplicatas.

### SaleItem

- produto previamente persistido;
- quantidade sempre positiva;
- preço unitário capturado no momento da inclusão;
- subtotal calculado por preço unitário e quantidade.

O preço unitário permanece imutável para preservar o valor histórico da venda,
mesmo que o preço atual do produto seja alterado posteriormente.

## Configuração local

O banco PostgreSQL deve ser criado fora da aplicação. O `SchemaInitializer`
cria as sequências e tabelas dentro do banco configurado.

Crie um arquivo `.env` na raiz do projeto:

```text
DB_URL=jdbc:postgresql://localhost:5432/projebac3_dev
DB_USER=postgres
DB_PASSWORD=sua_senha
```

O `.env` contém dados locais e não deve ser enviado ao Git. A `DatabaseConfig`
carrega esses valores com DotEnv e os entrega à `ConnectionFactory`.

Para testes de integração, é utilizado um banco separado, evitando que os
testes alterem dados de desenvolvimento:

```text
TEST_DB_URL=jdbc:postgresql://localhost:5432/projebac3_test
TEST_DB_USER=postgres
TEST_DB_PASSWORD=sua_senha
```

## Compilação

```bash
mvn test
```

Esse comando executa:

- `DatabaseEnvironmentTest`, que valida a conexão e a existência das tabelas;
- `ClientDAOTest`, com o CRUD de clientes;
- `ProductDAOTest`, com o CRUD de produtos.

A classe-base `DaoIntegrationTestSupport` inicializa o schema e limpa as duas
tabelas antes e depois de cada teste DAO. Ela utiliza somente as variáveis
`TEST_DB_*` e rejeita uma URL de testes igual à URL de desenvolvimento.

## Etapas do projeto

- [x] Criar as entidades de domínio `Client` e `Product`.
- [x] Criar os schemas de cliente e produto.
- [x] Definir a interface genérica de CRUD.
- [x] Implementar o ciclo JDBC no `AbstractDAO`.
- [x] Implementar `ClientDAO` e `ProductDAO`.
- [x] Criar `ConnectionFactory` e `DataAccessException`.
- [x] Adicionar DotEnv e proteger o `.env` no Git.
- [x] Criar `DatabaseConfig` para carregar o `.env`.
- [x] Criar `SchemaInitializer` para executar os schemas uma vez.
- [x] Criar e validar banco PostgreSQL exclusivo para testes.
- [x] Implementar factories para os dados dos testes.
- [x] Implementar testes de integração dos DAOs.
- [x] Modelar `SaleStatus`, `SaleItem` e `Sale`.
- [ ] Criar a entidade `Stock` e separar o saldo de `Product`.
- [ ] Criar schemas de estoque, venda e itens com chaves estrangeiras.
- [ ] Implementar persistência transacional de vendas e estoque.
- [ ] Criar services com validações e regras de negócio.
- [ ] Criar DTOs e controllers.
- [ ] Montar as dependências e iniciar a aplicação pelo `Main`.
- [ ] Criar testes unitários de services e controllers.

## Segurança

Credenciais reais não devem ser escritas no código, adicionadas aos resources
ou enviadas ao repositório. O arquivo `.env` deve permanecer apenas no ambiente
local de cada desenvolvedor.
