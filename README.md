# projEBAC3 - Módulo 30

Projeto de estudo desenvolvido em Java 17 para praticar persistência com JDBC e
PostgreSQL, sem Spring, JPA, Hibernate ou outro ORM.

O projeto está sendo reconstruído a partir de uma implementação JDBC antiga,
com o objetivo de separar responsabilidades, reduzir repetição nos DAOs e
preparar uma arquitetura em camadas.

## Estado atual

Este checkpoint contém a base da camada de persistência:

- entidades `Client` e `Product`;
- schemas PostgreSQL para clientes e produtos;
- contrato genérico de CRUD;
- `AbstractDAO` com o ciclo JDBC compartilhado;
- `ClientDAO` e `ProductDAO` com SQL e mapeamentos específicos;
- `ConnectionFactory` que entrega novas conexões;
- `DatabaseConfig` para carregar credenciais de desenvolvimento e teste;
- `SchemaInitializer` para preparar as tabelas antes do uso;
- `DataAccessException` para falhas de persistência;
- dependência DotEnv e arquivo `.env` local ignorado pelo Git.

O ambiente de integração possui um teste automatizado que valida a conexão com
o PostgreSQL de testes e a criação das tabelas dos DAOs.

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
- `price`, representado por `BigDecimal`;
- `stock`;
- disponibilidade calculada por `isInStock()`.

Preço e estoque não podem ser negativos segundo as constraints do schema.

## Configuração local

O banco PostgreSQL deve ser criado fora da aplicação. O `SchemaInitializer`
cria as sequências e tabelas dentro do banco configurado.

Crie um arquivo `.env` na raiz do projeto:

```text
DB_URL=jdbc:postgresql://localhost:5432/projebac3_dev
DB_USER=postgres
DB_PASSWORD=sua_senha
```

O `.env` contém dados locais e não deve ser enviado ao Git. A futura
`DatabaseConfig` será responsável por carregar esses valores com DotEnv e
entregá-los à `ConnectionFactory`.

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

Esse comando também executa `DatabaseEnvironmentTest`, que valida a conexão com
o banco de testes e confirma a existência de `tb_client` e `tb_product`.

A classe-base `DaoIntegrationTestSupport` inicializa o schema e limpa as duas
tabelas antes e depois de cada teste DAO. Ela utiliza somente as variáveis
`TEST_DB_*` e rejeita uma URL de testes igual à URL de desenvolvimento.

## Etapas do projeto

- [x] Criar as entidades de domínio.
- [x] Criar os schemas de cliente e produto.
- [x] Definir a interface genérica de CRUD.
- [x] Implementar o ciclo JDBC no `AbstractDAO`.
- [x] Implementar `ClientDAO` e `ProductDAO`.
- [x] Criar `ConnectionFactory` e `DataAccessException`.
- [x] Adicionar DotEnv e proteger o `.env` no Git.
- [x] Criar `DatabaseConfig` para carregar o `.env`.
- [x] Criar `SchemaInitializer` para executar os schemas uma vez.
- [x] Criar e validar banco PostgreSQL exclusivo para testes.
- [ ] Implementar testes de integração dos DAOs.
- [ ] Criar services com validações e regras de negócio.
- [ ] Criar DTOs e controllers.
- [ ] Montar as dependências e iniciar a aplicação pelo `Main`.
- [ ] Criar testes unitários de services e controllers.

## Segurança

Credenciais reais não devem ser escritas no código, adicionadas aos resources
ou enviadas ao repositório. O arquivo `.env` deve permanecer apenas no ambiente
local de cada desenvolvedor.
