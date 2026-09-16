package br.com.fabioperettig.controller;

import br.com.fabioperettig.dao.ClienteDAO;
import br.com.fabioperettig.dao.EstoqueDAO;
import br.com.fabioperettig.dao.ProdutoDAO;
import br.com.fabioperettig.dao.VendaDAO;
import br.com.fabioperettig.domain.Cliente;
import br.com.fabioperettig.domain.Produto;
import br.com.fabioperettig.domain.Venda;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.MaisDeUmRegistroException;
import br.com.fabioperettig.exceptions.TableException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class Controller {

    private final ClienteDAO clienteDao;
    private final ProdutoDAO produtoDao;
    private final EstoqueDAO estoqueDao;
    private final VendaDAO vendaDao;
    private final Scanner scanner;

    public Controller(ClienteDAO clienteDao, ProdutoDAO produtoDao,
                      EstoqueDAO estoqueDao, VendaDAO vendaDao,
                      Scanner scanner) {

        this.clienteDao = clienteDao;
        this.produtoDao = produtoDao;
        this.estoqueDao = estoqueDao;
        this.vendaDao = vendaDao;
        this.scanner = scanner;

    }

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
            } catch (DAOException e) {
                System.out.println("Operação não concluída. Confira a configuração do banco, os dados, "
                        + "os vínculos existentes e o saldo de estoque.");
            } catch (MaisDeUmRegistroException | TableException | TipoChaveNaoEncontradaException e) {
                System.out.println("Não foi possível identificar o registro: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Dados inválidos: " + e.getMessage());
            } catch (NoSuchElementException e) {
                return;
            }
        }
    }

    /// MENUS
    private void clienteMenu() throws DAOException,
            TipoChaveNaoEncontradaException,
            MaisDeUmRegistroException,
            TableException {

        while (true) {

            System.out.println("""
                
                === Clientes ===
                1 - Cadastrar
                2 - Consultar
                3 - Alterar cadastro
                4 - Listar todos
                5 - Excluir
                0 - Voltar
                """);
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> cadastrarCliente();
                case "2" -> {
                    Cliente cliente = consultarCliente();
                    System.out.printf("CPF: %s | Nome: %s | Ativo: %s%n",
                            cliente.getCpf(), cliente.getNome(), cliente.getAtivo());
                }
                case "3" -> alterarCliente();
                case "4" -> listarClientes();
                case "5" -> excluirCliente();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void produtoMenu() throws DAOException,
            MaisDeUmRegistroException,
            TableException,
            TipoChaveNaoEncontradaException {

        while (true) {

            System.out.println("""
                
                === Produtos ===
                1 - Cadastrar
                2 - Consultar
                3 - Alterar Dados
                4 - Listar Produtos e Estoque
                5 - Aumentar Estoque
                6 - Excluir Produto
                0 - Voltar
                """);
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> cadastrarProduto();
                case "2" -> {
                    Produto produto = consultarProduto();
                    System.out.printf("Código: %s | Nome: %s | Valor: %s | Cupom: %s%n",
                            produto.getCodigo(), produto.getNome(),
                            valorParaExibicao(produto), produto.getCupom15Off());
                }
                case "3" -> alterarProduto();
                case "4" -> listarProdutos();
                case "5" -> aumentarEstoque();
                case "6" -> excluirProduto();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void estoqueMenu() throws DAOException, MaisDeUmRegistroException, TableException {
        while (true) {
            System.out.println("""
                    === Estoque ===
                    1 - Listar produtos e estoque
                    2 - Aumentar estoque
                    0 - Voltar
                    """);
            switch (texto("Opção")) {
                case "1" -> listarProdutos();
                case "2" -> aumentarEstoque();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void vendasMenu() throws DAOException,
            MaisDeUmRegistroException,
            TableException,
            TipoChaveNaoEncontradaException {

        while (true) {

            System.out.println("""
                
                === Vendas ===
                1 - Cadastrar
                2 - Listar Vendas
                3 - Finalizar venda
                4 - Cancelar venda
                0 - Voltar
                """);
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> cadastrarVenda();
                case "2" -> listarVendas();
                case "3" -> atualizarVenda(true);
                case "4" -> atualizarVenda(false);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    ///CLIENTES
    private void cadastrarCliente() throws TipoChaveNaoEncontradaException, DAOException {
        Cliente cliente = new Cliente();
        cliente.setCpf(numeroLongo("CPF (somente números)"));
        preencherCliente(cliente);
        if (clienteDao.cadastrar(cliente)) System.out.println("Cliente cadastrado. ID: " + cliente.getId());
    }

    private void alterarCliente() throws
            MaisDeUmRegistroException,
            TableException,
            DAOException,
            TipoChaveNaoEncontradaException
    {
        Cliente cliente = consultarCliente();
        preencherCliente(cliente);
        clienteDao.alterar(cliente);
        System.out.println("Cliente atualizado.");
    }

    private Cliente consultarCliente() throws
            MaisDeUmRegistroException,
            TableException,
            DAOException
    {
        Cliente cliente = clienteDao.consultar(numeroLongo("CPF (somente números)"));
        if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado.");
        return cliente;
    }

    private void listarClientes() throws DAOException {
        var clientes = clienteDao.buscarTodos();
        if (clientes.isEmpty()) System.out.println("Nenhum cliente cadastrado.");
        for (Cliente cliente : clientes) {
            System.out.printf("CPF: %s | Nome: %s | Ativo: %s%n",
                    cliente.getCpf(), cliente.getNome(), cliente.getAtivo());
        }
    }

    private void excluirCliente() throws MaisDeUmRegistroException, DAOException, TableException {
        Cliente cliente = consultarCliente();
        clienteDao.excluir(cliente.getCpf());
        System.out.println("Cliente excluído.");
    }

    ///PRODUTOS
    private void cadastrarProduto() throws DAOException {
        Produto produto = new Produto();
        produto.setCodigo(texto("Código do produto"));
        preencherProduto(produto);
        int quantidade = inteiro("Quantidade inicial em estoque", 0);
        if (produtoDao.cadastrar(produto, quantidade)) System.out.println("Produto e estoque cadastrados.");
    }

    private Produto consultarProduto() throws MaisDeUmRegistroException, TableException, DAOException {
        Produto produto = produtoDao.consultar(texto("Código do produto"));
        if (produto == null) throw new IllegalArgumentException("Produto não encontrado.");
        return produto;
    }

    private String valorParaExibicao(Produto produto) {
        BigDecimal valor = produto.getValor().setScale(2, RoundingMode.HALF_UP);
        if (Boolean.TRUE.equals(produto.getCupom15Off())) {
            BigDecimal comDesconto = valor.multiply(new BigDecimal("0.85"))
                    .setScale(2, RoundingMode.HALF_UP);
            return "R$ " + comDesconto.toPlainString()
                    + " (15% OFF; original: R$ " + valor.toPlainString() + ")";
        }
        return "R$ " + valor.toPlainString();
    }

    private void preencherProduto(Produto produto) {
        produto.setNome(texto("Nome"));
        produto.setDescricao(texto("Descrição"));
        BigDecimal valor = new BigDecimal(texto("Valor (ex.: 19,90)").replace(',', '.'));
        if (valor.signum() < 0 || valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Use um valor não negativo com até duas casas decimais.");
        }
        produto.setValor(valor);
        produto.setCupom15Off(booleano("Cupom 15% OFF? (s/n)"));
    }

    private void alterarProduto() throws
            MaisDeUmRegistroException,
            TableException,
            DAOException,
            TipoChaveNaoEncontradaException
    {
        Produto produto = consultarProduto();
        preencherProduto(produto);
        produtoDao.alterar(produto);
        System.out.println("Produto atualizado.");
    }

    private void listarProdutos() throws
            DAOException,
            MaisDeUmRegistroException,
            TableException
    {
        var produtos = produtoDao.buscarTodos();
        if (produtos.isEmpty()) System.out.println("Nenhum produto cadastrado.");
        for (Produto produto : produtos) {
            var estoque = estoqueDao.consultar(produto.getId());
            System.out.printf("Código: %s | Nome: %s | Valor: %s | Cupom: %s | Estoque: %s%n",
                    produto.getCodigo(), produto.getNome(), valorParaExibicao(produto), produto.getCupom15Off(),
                    estoque == null ? "não cadastrado" : estoque.getQuantidade());
        }
    }

    private void aumentarEstoque() throws
            MaisDeUmRegistroException,
            DAOException,
            TableException
    {
        Produto produto = consultarProduto();
        estoqueDao.adicionar(produto.getId(), inteiro("Quantidade de entrada", 1));
        System.out.println("Estoque atualizado.");
    }

    private void excluirProduto() throws
            MaisDeUmRegistroException,
            DAOException,
            TableException
    {
        Produto produto = consultarProduto();
        produtoDao.excluir(produto.getCodigo());
        System.out.println("Produto excluído.");
    }


    ///VENDAS
    private void cadastrarVenda() throws
            MaisDeUmRegistroException,
            TableException,
            DAOException,
            TipoChaveNaoEncontradaException
    {
        Venda venda = new Venda();
        venda.setCodigo(texto("Código da venda"));
        venda.setCliente(consultarCliente());
        venda.setStatus(Venda.Status.INICIADA);
        do {
            venda.adicionarProduto(consultarProduto(), inteiro("Quantidade vendida", 1));
        } while (booleano("Adicionar outro produto? (s/n)"));
        venda.setDataVenda(Instant.now());
        if (vendaDao.cadastrar(venda)) System.out.println("Venda cadastrada. Total: " + venda.getValorTotal());
    }

    private void atualizarVenda(boolean finalizar) throws
            DAOException,
            MaisDeUmRegistroException,
            TableException,
            TipoChaveNaoEncontradaException
    {
        Venda venda = vendaDao.consultar(texto("Código da venda"));
        if (venda == null) throw new IllegalArgumentException("Venda não encontrada.");
        if (finalizar) vendaDao.finalizarVenda(venda);
        else vendaDao.cancelarVenda(venda);
        System.out.println("Situação da venda: " + venda.getStatus());
    }

    private void listarVendas() throws DAOException {
        var vendas = vendaDao.buscarTodos();
        if (vendas.isEmpty()) System.out.println("Nenhuma venda cadastrada.");
        for (Venda venda : vendas) {
            System.out.printf("Código: %s | Cliente: %s | Total: %s | Situação: %s%n",
                    venda.getCodigo(), venda.getCliente().getNome(),
                    venda.getValorTotal(), venda.getStatus());
        }
    }


    ///AUXILIARES
    private String texto(String campo) {
        System.out.print(campo + ": ");
        String valor = scanner.nextLine().trim();
        if (valor.isEmpty()) throw new IllegalArgumentException("O campo não pode ficar vazio.");
        return valor;
    }

    private long numeroLongo(String campo) {
        String valor = texto(campo);
        if (!valor.matches("[0-9]+")) throw new IllegalArgumentException("Informe somente números.");
        return Long.parseLong(valor);
    }

    private int inteiro(String campo, int minimo) {
        int valor = Integer.parseInt(texto(campo));
        if (valor < minimo) throw new IllegalArgumentException("A quantidade mínima é " + minimo + ".");
        return valor;
    }

    private boolean booleano(String campo) {
        String valor = texto(campo);
        if (valor.equalsIgnoreCase("s")) return true;
        if (valor.equalsIgnoreCase("n")) return false;
        throw new IllegalArgumentException("Responda s ou n.");
    }

    private void preencherCliente(Cliente cliente) {
        cliente.setNome(texto("Nome"));
        cliente.setTel(numeroLongo("Telefone (somente números)"));
        cliente.setEnd(texto("Endereço"));
        cliente.setNumero(inteiro("Número do endereço", 0));
        cliente.setCidade(texto("Cidade"));
        cliente.setEstado(texto("Estado"));
        cliente.setAtivo(booleano("Cliente ativo? (s/n)"));
    }
}
