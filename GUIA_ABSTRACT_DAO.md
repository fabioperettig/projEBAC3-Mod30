# Guia da classe `AbstractDAO`

## 1. Objetivo da classe

A `AbstractDAO<T, ID>` concentra o algoritmo JDBC que é igual para todas as
entidades do projeto. Ela implementa uma única vez as cinco operações declaradas
por `IGenericDAO<T, ID>`:

- `create(T entity)`;
- `findById(ID id)`;
- `findAll()`;
- `update(T entity)`;
- `deleteById(ID id)`.

O `ClientDAO` e o `ProductDAO` não precisam repetir abertura de conexão,
criação de `PreparedStatement`, execução, leitura de `ResultSet`, fechamento de
recursos e conversão de `SQLException`.

As classes concretas informam apenas o que varia entre as entidades:

- o texto de cada comando SQL;
- como associar os valores ao `PreparedStatement`;
- como converter uma linha do banco em uma entidade;
- como ler e atribuir o ID gerado.

Esse desenho é uma aplicação do padrão **Template Method**. A classe abstrata
define o fluxo completo da operação, enquanto subclasses fornecem etapas
específicas desse fluxo.

```text
IGenericDAO<T, ID>
        ↑
AbstractDAO<T, ID>
  ├── controla o ciclo JDBC
  ├── implementa o CRUD
  └── declara pontos abstratos
        ↑
  ┌─────┴─────┐
ClientDAO  ProductDAO
```

## 2. Estado atual da revisão

A ideia geral da classe está correta, mas a versão revisada ainda não compila.
Três assinaturas abstratas precisam ser corrigidas.

### `bindId` deve receber `ID`

Está assim:

```java
protected abstract void bindId(
        PreparedStatement statement,
        T entity
) throws SQLException;
```

O método é usado por `findById(ID id)` e `deleteById(ID id)`. Portanto, seu
segundo parâmetro deve ser `ID`, não `T`:

```java
protected abstract void bindId(
        PreparedStatement statement,
        ID id
) throws SQLException;
```

`T` representa a entidade inteira, como `Client`. `ID` representa somente seu
identificador, como `Long`.

### `readGeneratedId` deve receber um `ResultSet`

Está assim:

```java
protected abstract ID readGeneratedId(T entity, ID generatedId);
```

Dentro de `create()`, o método recebe `generatedKeys`, que é um `ResultSet`.
Sua assinatura correta é:

```java
protected abstract ID readGeneratedId(
        ResultSet generatedKeys
) throws SQLException;
```

Uma implementação de `ClientDAO`, por exemplo, poderá fazer:

```java
@Override
protected Long readGeneratedId(ResultSet generatedKeys)
        throws SQLException {
    return generatedKeys.getLong(1);
}
```

### `setGeneratedId` precisa ser declarado

O método é chamado por `create()`, mas não existe entre os métodos abstratos.
Ele deve ser declarado assim:

```java
protected abstract void setGeneratedId(
        T entity,
        ID generatedId
);
```

O `ClientDAO` poderá implementá-lo desta maneira:

```java
@Override
protected void setGeneratedId(Client client, Long generatedId) {
    client.setId(generatedId);
}
```

As três assinaturas corrigidas, em conjunto, ficam assim:

```java
protected abstract void bindId(
        PreparedStatement statement,
        ID id
) throws SQLException;

protected abstract ID readGeneratedId(
        ResultSet generatedKeys
) throws SQLException;

protected abstract void setGeneratedId(
        T entity,
        ID generatedId
);
```

Também existe um pequeno erro de digitação na mensagem `Failed to create
enity`. O texto correto é `Failed to create entity`.

Além disso, as classes concretas precisarão usar os dois tipos genéricos:

```java
public class ClientDAO extends AbstractDAO<Client, Long> {
}

public class ProductDAO extends AbstractDAO<Product, Long> {
}
```

Elas não precisam declarar `implements IGenericDAO` novamente, pois
`AbstractDAO` já implementa essa interface.

## 3. Significado de `<T, ID>`

Na declaração:

```java
public abstract class AbstractDAO<T, ID>
        implements IGenericDAO<T, ID>
```

`T` representa o tipo da entidade e `ID` representa o tipo de seu identificador.

Para clientes:

```java
AbstractDAO<Client, Long>
```

Nesse caso:

- todo `T` da classe passa a representar `Client`;
- todo `ID` passa a representar `Long`.

Para produtos:

```java
AbstractDAO<Product, Long>
```

A classe abstrata consegue executar o mesmo algoritmo sem conhecer previamente
qual entidade será utilizada.

## 4. Campo `connectionFactory`

```java
private final ConnectionFactory connectionFactory;
```

Esse campo guarda a fábrica usada para solicitar uma nova conexão em cada
operação.

Ele é `private` porque somente o próprio `AbstractDAO` deve controlar a abertura
das conexões. As subclasses não precisam abrir conexões diretamente.

Ele é `final` porque a fábrica não deve ser substituída depois da construção do
DAO.

O campo não guarda uma `Connection`. Ele guarda somente o objeto capaz de criar
conexões. Isso evita compartilhar uma conexão global entre operações.

## 5. Construtor

```java
protected AbstractDAO(ConnectionFactory connectionFactory) {
    this.connectionFactory = Objects.requireNonNull(
            connectionFactory,
            "ConnectionFactory is required"
    );
}
```

O construtor recebe a dependência em vez de executar `new ConnectionFactory()`.
Isso é injeção de dependência pelo construtor.

`protected` permite que o construtor seja chamado pelas subclasses, mas impede
que alguém tente instanciar `AbstractDAO` diretamente.

`Objects.requireNonNull()` interrompe imediatamente a construção caso a fábrica
seja nula. Sem essa verificação, o erro apareceria apenas ao tentar executar uma
operação no banco, tornando a causa mais difícil de identificar.

O construtor do `ClientDAO` ficará parecido com:

```java
public ClientDAO(ConnectionFactory connectionFactory) {
    super(connectionFactory);
}
```

`super(connectionFactory)` entrega a fábrica ao construtor do `AbstractDAO`.

## 6. Métodos abstratos de SQL

### `getInsertSql()`

```java
protected abstract String getInsertSql();
```

Retorna o SQL usado por `create()`.

Exemplo para cliente:

```sql
INSERT INTO tb_client (name_client) VALUES (?)
```

### `getFindByIdSql()`

```java
protected abstract String getFindByIdSql();
```

Retorna a consulta usada por `findById()`.

```sql
SELECT id, name_client FROM tb_client WHERE id = ?
```

### `getFindAllSql()`

```java
protected abstract String getFindAllSql();
```

Retorna a consulta usada por `findAll()`. É recomendável listar as colunas
explicitamente e definir uma ordenação.

```sql
SELECT id, name_client FROM tb_client ORDER BY id
```

### `getUpdateSql()`

```java
protected abstract String getUpdateSql();
```

Retorna o comando usado por `update()`.

```sql
UPDATE tb_client SET name_client = ? WHERE id = ?
```

### `getDeleteByIdSql()`

```java
protected abstract String getDeleteByIdSql();
```

Retorna o comando usado por `deleteById()`.

```sql
DELETE FROM tb_client WHERE id = ?
```

Esses métodos permanecem abstratos porque o `AbstractDAO` não conhece nomes de
tabelas, colunas ou regras de consulta de cada entidade.

## 7. Métodos abstratos de associação de parâmetros

### `bindInsert()`

```java
protected abstract void bindInsert(
        PreparedStatement statement,
        T entity
) throws SQLException;
```

Associa os valores da entidade aos `?` do `INSERT`.

```java
statement.setString(1, client.getName());
```

A ordem dos `set...()` deve corresponder exatamente à ordem dos `?` no SQL.

### `bindUpdate()`

```java
protected abstract void bindUpdate(
        PreparedStatement statement,
        T entity
) throws SQLException;
```

Associa os valores usados pelo `UPDATE`, incluindo o identificador do `WHERE`.

Para este SQL:

```sql
UPDATE tb_client SET name_client = ? WHERE id = ?
```

o vínculo deve seguir a mesma ordem:

```java
statement.setString(1, client.getName());
statement.setLong(2, client.getId());
```

### `bindId()`

```java
protected abstract void bindId(
        PreparedStatement statement,
        ID id
) throws SQLException;
```

Associa o identificador ao primeiro `?` das operações `findById()` e
`deleteById()`.

```java
statement.setLong(1, id);
```

O uso de `PreparedStatement` evita concatenar entrada no SQL e faz o driver
tratar corretamente o tipo do valor.

## 8. Métodos abstratos de mapeamento

### `mapRow()`

```java
protected abstract T mapRow(ResultSet resultSet)
        throws SQLException;
```

Converte a linha atual do `ResultSet` em uma entidade.

Exemplo:

```java
Client client = new Client();
client.setId(resultSet.getLong("id"));
client.setName(resultSet.getString("name_client"));
return client;
```

`mapRow()` não chama `resultSet.next()`. Quem movimenta o cursor é o algoritmo
do `AbstractDAO`. O método apenas lê a linha na posição atual.

O mesmo mapeamento é reutilizado por `findById()` e `findAll()`, evitando código
duplicado.

### `readGeneratedId()`

```java
protected abstract ID readGeneratedId(
        ResultSet generatedKeys
) throws SQLException;
```

Lê do `ResultSet` o ID criado pelo banco após o `INSERT`.

```java
return generatedKeys.getLong(1);
```

O retorno é `ID`, e não obrigatoriamente `Long`, porque a classe foi definida de
forma genérica.

### `setGeneratedId()`

```java
protected abstract void setGeneratedId(
        T entity,
        ID generatedId
);
```

Coloca o ID retornado pelo banco na entidade que foi cadastrada.

```java
client.setId(generatedId);
```

Assim, o objeto devolvido por `create()` já contém seu identificador.

## 9. Método `create()`

```java
public T create(T entity)
```

O fluxo do método é:

1. rejeitar uma entidade nula;
2. solicitar uma conexão à `ConnectionFactory`;
3. preparar o SQL de inserção;
4. pedir ao driver que devolva as chaves geradas;
5. chamar `bindInsert()`;
6. executar o `INSERT`;
7. confirmar que exatamente uma linha foi inserida;
8. obter o ID gerado;
9. colocar esse ID na entidade;
10. retornar a entidade preenchida;
11. fechar todos os recursos automaticamente.

### Verificação de nulidade

```java
Objects.requireNonNull(entity, "Entity is required");
```

Evita executar uma operação inválida e produzir um erro menos claro durante a
associação dos parâmetros.

### Solicitação das chaves geradas

```java
connection.prepareStatement(
        getInsertSql(),
        Statement.RETURN_GENERATED_KEYS
);
```

`Statement.RETURN_GENERATED_KEYS` informa ao driver JDBC que o código deseja
ler o identificador criado pelo banco.

### Execução e quantidade de linhas

```java
int affectedRows = statement.executeUpdate();
```

Apesar do nome, `executeUpdate()` é usado para `INSERT`, `UPDATE` e `DELETE`. Ele
retorna a quantidade de linhas afetadas.

Para cadastrar uma entidade, o resultado esperado é exatamente `1`.

### Leitura da chave

```java
try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
    if (!generatedKeys.next()) {
        throw new DataAccessException(
                "Database did not return the generated ID"
        );
    }

    ID generatedId = readGeneratedId(generatedKeys);
    setGeneratedId(entity, generatedId);
}
```

`next()` move o cursor para a primeira chave. Se não existir uma linha, o banco
ou driver não devolveu o ID esperado.

## 10. Método `findById()`

```java
public Optional<T> findById(ID id)
```

O fluxo é:

1. rejeitar ID nulo;
2. abrir conexão e preparar o `SELECT`;
3. chamar `bindId()`;
4. executar a consulta;
5. devolver `Optional.empty()` quando não houver resultado;
6. chamar `mapRow()` quando houver uma linha;
7. devolver `Optional.of(entity)`;
8. fechar os recursos.

O trecho:

```java
if (!resultSet.next()) {
    return Optional.empty();
}
```

expressa que não encontrar um registro é um resultado normal de uma busca, não
uma falha do banco.

O uso de `Optional<T>` evita retornar `null` e obriga quem chama o método a
considerar a possibilidade de ausência.

## 11. Método `findAll()`

```java
public List<T> findAll()
```

O método cria uma lista inicialmente vazia, executa o `SELECT` e percorre todas
as linhas:

```java
while (resultSet.next()) {
    entities.add(mapRow(resultSet));
}
```

Cada chamada a `next()` avança o cursor uma linha. `mapRow()` transforma essa
linha em uma entidade, que é adicionada à lista.

Se a tabela estiver vazia, o método retorna uma lista vazia. Ele não retorna
`null` e não precisa usar `Optional<List<T>>`.

## 12. Método `update()`

```java
public boolean update(T entity)
```

O fluxo é:

1. rejeitar entidade nula;
2. preparar o SQL de atualização;
3. chamar `bindUpdate()`;
4. executar o comando;
5. retornar se exatamente uma linha foi alterada.

```java
return affectedRows == 1;
```

Quando o ID não existe, o PostgreSQL retorna zero linhas afetadas e o método
devolve `false`.

Como o `WHERE` usa uma chave primária, não deveria ser possível alterar mais de
uma linha. Se isso ocorrer, o retorno também será `false`, indicando que o
resultado não foi o esperado.

Validações como nome vazio ou regras de negócio não pertencem a este método.
Elas deverão ser realizadas pelo service e reforçadas por constraints no banco.

## 13. Método `deleteById()`

```java
public boolean deleteById(ID id)
```

Seu fluxo é semelhante ao de `update()`:

1. rejeitar ID nulo;
2. preparar o `DELETE`;
3. chamar `bindId()`;
4. executar o comando;
5. retornar `true` somente se uma linha for removida.

O método recebe apenas o ID porque não precisa carregar ou receber uma entidade
inteira para executar:

```sql
DELETE FROM tb_client WHERE id = ?
```

Decidir se o registro pode ser excluído é responsabilidade do service. O DAO
apenas executa a persistência solicitada.

## 14. `try-with-resources`

Os blocos abaixo são centrais para a segurança do código:

```java
try (
        Connection connection = connectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()
) {
    // uso dos recursos
}
```

Ao sair do bloco, com sucesso ou exceção, Java chama `close()` automaticamente.
Os recursos são fechados na ordem inversa:

1. `ResultSet`;
2. `PreparedStatement`;
3. `Connection`.

Isso substitui os blocos `finally` e os métodos manuais de fechamento do modelo
antigo.

## 15. Tratamento de `SQLException`

Cada operação captura `SQLException` e a converte:

```java
catch (SQLException exception) {
    throw new DataAccessException(
            "Failed to update entity",
            exception
    );
}
```

`DataAccessException` é uma exceção não verificada, pois herda de
`RuntimeException`. Assim, a interface genérica não precisa declarar
`throws Exception`.

A `SQLException` original é passada como causa. Isso preserva informações como
mensagem do PostgreSQL, SQLState e stack trace para diagnóstico.

O DAO traduz falhas técnicas. Futuramente, o service poderá lançar exceções de
negócio, como validação ou entidade não encontrada, sem conhecer JDBC.

## 16. Responsabilidades que não pertencem ao `AbstractDAO`

O `AbstractDAO` não deve:

- executar arquivos de schema;
- validar regras de negócio;
- criar DTOs;
- exibir mensagens ao usuário;
- conhecer `Client` ou `Product` diretamente;
- montar SQL por reflexão;
- armazenar uma conexão global;
- decidir transações de casos de uso com várias operações.

Cada método atual abre sua própria conexão e executa uma única instrução. Isso é
adequado para o CRUD inicial.

Se futuramente um caso de uso precisar, por exemplo, baixar estoque e cadastrar
uma venda na mesma transação, as duas operações deverão compartilhar uma
conexão controlada por um gerenciador de transações. Não se deve tentar resolver
essa necessidade espalhando `commit()` e `rollback()` pelos DAOs agora.

## 17. Resumo do fluxo entre as classes

Quando o código executar:

```java
clientDAO.findById(10L);
```

ocorrerá este fluxo:

```text
ClientDAO recebe findById(10L)
        ↓
AbstractDAO executa o algoritmo genérico
        ↓
ClientDAO fornece o SELECT específico
        ↓
AbstractDAO abre a conexão
        ↓
ClientDAO associa 10L ao PreparedStatement
        ↓
AbstractDAO executa a consulta
        ↓
ClientDAO converte a linha em Client
        ↓
AbstractDAO fecha os recursos
        ↓
Retorna Optional<Client>
```

Essa divisão mantém o JDBC explícito para aprendizado, mas elimina a repetição
do ciclo técnico em cada DAO concreto.

## 18. Checklist antes de seguir para `ClientDAO`

- Corrigir `bindId()` para receber `ID`.
- Corrigir `readGeneratedId()` para receber `ResultSet` e lançar
  `SQLException`.
- Declarar `setGeneratedId(T entity, ID generatedId)`.
- Corrigir `enity` para `entity` na mensagem de erro.
- Alterar `ClientDAO` para `AbstractDAO<Client, Long>`.
- Alterar `ProductDAO` para `AbstractDAO<Product, Long>`.
- Criar construtores nos DAOs concretos e chamar `super(connectionFactory)`.
- Implementar nos DAOs concretos todos os pontos abstratos.
- Executar `mvn test` novamente.

