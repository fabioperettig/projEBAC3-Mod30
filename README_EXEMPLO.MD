![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Projeto Curso EBAC](https://img.shields.io/badge/Projeto--Curso--EBAC-navy?style=for-the-badge)

# ☕ Projeto EBAC – Persistência de Dados com Java JDBC, Generics & PostgreSQL

**Este projeto implementa um CRUD completo utilizando JDBC e PostgreSQL, sem o auxílio de frameworks como Spring Data ou Hibernate.**

Além da implementação proposta pelo curso, este projeto recebeu uma série de melhorias arquiteturais desenvolvidas por iniciativa própria, incluindo a utilização de Generics, abstração da camada DAO, centralização de consultas SQL compartilhadas e otimização da suíte de testes.

<details><Summary>Objetivos do projeto</Summary>

* Implementar um CRUD completo utilizando JDBC
* Praticar SQL e PostgreSQL integrado ao Java
* Abstração de Classe DAO
* Aplicar Generics para reduzir duplicação de código (DRY)
* Criar testes automatizados via JUnit
</details>

<br>O projeto foi estruturado com foco na camada de persistência, concentrando o estudo na implementação do padrão DAO e na comunicação direta entre Java e PostgreSQL através do JDBC.

````
src
├── main
│   ├── factory
│   │   └── ConnectionFactory
│   │
│   ├── jdbc
│   │   ├── dao
│   │   │   ├── InterfaceDAO<T>
│   │   │   ├── AbstractDAO<T>
│   │   │   ├── ClienteDAO
│   │   │   └── ProdutoDAO
│   │   │
│   │   └── domain
│   │       ├── Cliente
│   │       └── Produto
│   │
│   ├── service
│   └── Main
│
├── resources
│   ├── config
│   │   └── database.properties
│   │
│   └── database
│       ├── schemaCliente.sql
│       └── schemaProduto.sql
│
└── test
    ├── ClienteTest
    ├── ProdutoTest
    └── SuiteTest
````


| Pacote | Responsabilidade |
| --- | --- |
| factory | Centraliza a criação da conexão com o PostgreSQL |
| jdbc/dao | Implementação do padrão DAO utilizando Interfaces, Generics e uma Classe Abstrata |
| jdbc/domain | Entidades utilizadas pela aplicação |
| resources/config | Configuração da conexão com o banco de dados |
| resources/database | Scripts SQL responsáveis pela criação automática das tabelas |
| test | Testes automatizados das operações CRUD utilizando JUnit |

## ⚙️ Implementação automática do banco

Os scripts responsáveis pela criação das tabelas ficam armazenados em resources/database.

Sempre que um DAO é inicializado, sua implementação verifica a existência do schema correspondente e executa automaticamente os comandos SQL necessários.

Dessa forma, basta criar um banco PostgreSQL vazio e configurar a conexão para que toda a estrutura seja criada sem depender de ferramentas externas.

````SQL
CREATE SEQUENCE IF NOT EXISTS SQ_CLIENTE
    START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS TB_CLIENTE (
    id BIGINT NOT NULL DEFAULT nextval('SQ_CLIENTE'),
    nome VARCHAR(50) NOT NULL,
    codigo VARCHAR(20) NOT NULL,

    CONSTRAINT PK_ID_CLIENTE PRIMARY KEY (ID),
    CONSTRAINT UK_CODIGO_CLIENTE UNIQUE (CODIGO)
);
````


````java
public abstract class AbstractDAO<T> {

    /// ÁREA DE CONEXÃO
    protected void executarSchema(Connection connection) throws Exception {

        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(getSchema());

        if (input == null) {
            throw new RuntimeException("ARQUIVO .sql NÃO ENCONTRADO NA PASTA RESOURCE.");
        }

        String sql;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        }

        String[] comandos = sql.split(";");

        for (String comando :  comandos) {
            if (!comando.isBlank()) {
                try (PreparedStatement statement = connection.prepareStatement(comando)) {
                    statement.execute();
                }
            }
        }
    }

    {...}
}
````
> O método abstrato **getSchema()** permite que o diretório do .sql correto seja definido diretamente na camada DAO, continuando válido para reutilização em quantos bancos o projeto precisar.


## 🏛️ Arquitetura DAO

````sql
InterfaceDAO<T>
        ▲
AbstractDAO<T>
        ▲

ClienteDAO | ProdutoDAO
````

A implementação utiliza uma **Interface Genérica** para definir o contrato das operações CRUD e uma Classe Abstrata responsável por concentrar toda a lógica compartilhada entre os DAOs, reduzindo código repetitivo (boilerplate) através dos princípios de reutilização e DRY (Don’t Repeat Yourself).

### Principais melhorias implementadas

| Implementação | Objetivo |
| --- | --- |
| Generics | Tornar o contrato DAO reutilizável |
| AbstractDAO | Centralizar lógica compartilhada |
| SQL reutilizável | Evitar duplicação entre entidades |
| Criação automática do schema | Facilitar execução em qualquer ambiente |
| Refatoração dos testes | Redução de boilerplate utilizando métodos auxiliares |

## ☑️ Como executar este projeto

1. Clone o repositório

2. Crie um banco PostgreSQL vazio

3. Configure as variáveis de ambiente

4. Execute os testes

----

### Fabio peretti Guimarães | Ebac mod 29 | JUL 2026