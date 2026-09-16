package br.com.fabioperettig;

import br.com.fabioperettig.dao.*;
import br.com.fabioperettig.dao.generic.jdbc.ConnectionFactory;
import br.com.fabioperettig.domain.*;
import br.com.fabioperettig.exceptions.DAOException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;

@org.junit.jupiter.api.extension.ExtendWith(BancoTeste.class)
public class EstoqueDAOTest {
    private final ProdutoDAO produtoDao = new ProdutoDAO();
    private final ClienteDAO clienteDao = new ClienteDAO();
    private final EstoqueDAO estoqueDao = new EstoqueDAO();
    private final VendaDAO vendaDao = new VendaDAO();
    private Cliente cliente;
    private Produto produto;

    @BeforeEach
    public void init() throws Exception {
        cliente = new Cliente();
        cliente.setNome("Cliente estoque");
        cliente.setCpf(98765432100L);
        cliente.setTel(9999999999L);
        cliente.setEnd("Rua A");
        cliente.setNumero(1);
        cliente.setCidade("São Paulo");
        cliente.setEstado("SP");
        cliente.setAtivo(false);
        clienteDao.cadastrar(cliente);
        produto = criarProduto("EST1", 10);
    }

    private Produto criarProduto(String codigo, int quantidade) throws Exception {
        Produto p = new Produto();
        p.setCodigo(codigo);
        p.setNome("Produto estoque");
        p.setDescricao("Teste estoque");
        p.setValor(BigDecimal.TEN);
        p.setCupom15Off(true);
        produtoDao.cadastrar(p, quantidade);
        return p;
    }

    private Venda criarVenda(String codigo, int quantidade) {
        Venda v = new Venda();
        v.setCodigo(codigo);
        v.setCliente(cliente);
        v.setDataVenda(Instant.now());
        v.setStatus(Venda.Status.INICIADA);
        v.adicionarProduto(produto, quantidade);
        return v;
    }

    @AfterEach
    public void end() throws Exception {
        try (var conn = ConnectionFactory.getConnection(); var stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM TB_PRODUTO_QUANTIDADE");
            stm.executeUpdate("DELETE FROM TB_VENDA");
        }
        for (Produto p : produtoDao.buscarTodos()) produtoDao.excluir(p.getCodigo());
        if (cliente != null) clienteDao.excluir(cliente.getCpf());
    }

    @Test
    public void persistirNovosCampos() throws Exception {
        assertFalse(clienteDao.consultar(cliente.getCpf()).getAtivo());
        assertTrue(produtoDao.consultar(produto.getCodigo()).getCupom15Off());
        assertFalse(clienteDao.buscarTodos().iterator().next().getAtivo());
        assertTrue(produtoDao.buscarTodos().iterator().next().getCupom15Off());
        cliente.setAtivo(true);
        produto.setCupom15Off(false);
        clienteDao.alterar(cliente);
        produtoDao.alterar(produto);
        assertTrue(clienteDao.consultar(cliente.getCpf()).getAtivo());
        assertFalse(produtoDao.consultar(produto.getCodigo()).getCupom15Off());
        assertTrue(clienteDao.buscarTodos().iterator().next().getAtivo());
        assertFalse(produtoDao.buscarTodos().iterator().next().getCupom15Off());
    }

    @Test
    public void cadastrarReporEExcluirEstoque() throws Exception {
        assertEquals(10, estoqueDao.consultar(produto.getId()).getQuantidade());
        estoqueDao.adicionar(produto.getId(), 5);
        assertEquals(15, estoqueDao.consultar(produto.getId()).getQuantidade());
        assertThrows(DAOException.class, () -> estoqueDao.adicionar(produto.getId(), -1));
        produtoDao.excluir(produto.getCodigo());
        assertNull(estoqueDao.consultar(produto.getId()));
    }

    @Test
    public void cadastrarProdutoSemQuantidade() throws Exception {
        Produto p = new Produto();
        p.setCodigo("EST0"); p.setNome("Zero"); p.setDescricao("Sem saldo"); p.setValor(BigDecimal.ONE);
        assertTrue(produtoDao.cadastrar(p));
        assertEquals(0, estoqueDao.consultar(p.getId()).getQuantidade());
    }

    @Test
    public void rejeitarQuantidadeInicialNegativa() throws Exception {
        assertThrows(DAOException.class, () -> criarProduto("ESTNEG", -1));
        assertNull(produtoDao.consultar("ESTNEG"));
        assertEquals(1, estoqueDao.buscarTodos().size());
    }

    @Test
    public void baixarEstoqueEDevolverUmaUnicaVez() throws Exception {
        Venda v = criarVenda("ESTV1", 2);
        vendaDao.cadastrar(v);
        assertEquals(8, estoqueDao.consultar(produto.getId()).getQuantidade());
        Venda consultada = vendaDao.consultar(v.getCodigo());
        assertFalse(consultada.getCliente().getAtivo());
        assertTrue(consultada.getProdutos().iterator().next().getProduto().getCupom15Off());
        vendaDao.finalizarVenda(v);
        vendaDao.finalizarVenda(v);
        assertEquals(8, estoqueDao.consultar(produto.getId()).getQuantidade());
        vendaDao.cancelarVenda(v);
        vendaDao.cancelarVenda(consultada);
        assertEquals(10, estoqueDao.consultar(produto.getId()).getQuantidade());
        assertEquals(Venda.Status.CANCELADA, vendaDao.consultar(v.getCodigo()).getStatus());
        assertThrows(DAOException.class, () -> vendaDao.finalizarVenda(v));
    }

    @Test
    public void desfazerVendaInteiraQuandoSegundoProdutoNaoTemSaldo() throws Exception {
        Produto semSaldo = criarProduto("EST2", 0);
        Venda v = criarVenda("ESTV2", 2);
        v.adicionarProduto(semSaldo, 1);
        assertThrows(DAOException.class, () -> vendaDao.cadastrar(v));
        assertNull(v.getId());
        assertNull(vendaDao.consultar(v.getCodigo()));
        assertEquals(10, estoqueDao.consultar(produto.getId()).getQuantidade());
        assertEquals(0, estoqueDao.consultar(semSaldo.getId()).getQuantidade());
        try (var conn = ConnectionFactory.getConnection(); var stm = conn.createStatement();
             var rs = stm.executeQuery("SELECT count(*) FROM TB_PRODUTO_QUANTIDADE")) {
            assertTrue(rs.next()); assertEquals(0, rs.getInt(1));
        }
    }

    @Test
    public void impedirVendaDuplicadaSemNovaBaixa() throws Exception {
        Venda v = criarVenda("ESTV3", 10);
        vendaDao.cadastrar(v);
        assertThrows(DAOException.class, () -> vendaDao.cadastrar(v));
        assertEquals(0, estoqueDao.consultar(produto.getId()).getQuantidade());
        assertThrows(DAOException.class, () -> vendaDao.cadastrar(criarVenda("ESTV4", 1)));
        assertNull(vendaDao.consultar("ESTV4"));
    }

    @Test
    public void buscarTodasVendasComAssociacoes() throws Exception {
        vendaDao.cadastrar(criarVenda("ESTV5", 1));
        vendaDao.cadastrar(criarVenda("ESTV6", 1));
        var vendas = vendaDao.buscarTodos();
        assertEquals(2, vendas.size());
        for (Venda v : vendas) {
            assertFalse(v.getCliente().getAtivo());
            assertEquals(1, v.getProdutos().size());
            assertTrue(v.getProdutos().iterator().next().getProduto().getCupom15Off());
        }
    }

    @Test
    public void exibirDescontoNaConsultaENaListagem() throws Exception {
        produto.setValor(new BigDecimal("19.90"));
        produtoDao.alterar(produto);
        String saida = executarMenuProdutos();
        String preco = "Valor: R$ 16.92 (15% OFF; original: R$ 19.90)";
        assertEquals(2, saida.split(java.util.regex.Pattern.quote(preco), -1).length - 1);
        assertTrue(saida.contains("Estoque: 10"));
        assertFalse(saida.contains("Operação não concluída"));
        assertEquals(new BigDecimal("19.90"), produtoDao.consultar(produto.getCodigo()).getValor());
    }

    @Test
    public void exibirValorIntegralSemCupom() throws Exception {
        produto.setCupom15Off(false);
        produtoDao.alterar(produto);
        String saida = executarMenuProdutos();
        assertEquals(2, saida.split(
                java.util.regex.Pattern.quote("Valor: R$ 10.00"), -1).length - 1);
        assertFalse(saida.contains("15% OFF"));
        assertTrue(saida.contains("Estoque: 10"));
    }

    private String executarMenuProdutos() {
        var saida = new java.io.ByteArrayOutputStream();
        var anterior = System.out;
        try (var impressao = new java.io.PrintStream(saida, true, java.nio.charset.StandardCharsets.UTF_8);
             var entrada = new java.util.Scanner("2\n2\nEST1\n4\n0\n0\n")) {
            System.setOut(impressao);
            new br.com.fabioperettig.controller.Controller(
                    clienteDao, produtoDao, estoqueDao, vendaDao, entrada).executar();
        } finally {
            System.setOut(anterior);
        }
        return saida.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

}
