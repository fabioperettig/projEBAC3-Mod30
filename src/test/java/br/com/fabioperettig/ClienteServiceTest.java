package br.com.fabioperettig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fabioperettig.dao.ClienteDaoMock;
import br.com.fabioperettig.dao.IClienteDAO;
import br.com.fabioperettig.domain.Cliente;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;
import br.com.fabioperettig.services.ClienteService;
import br.com.fabioperettig.services.IClienteService;

public class ClienteServiceTest {
	
	private IClienteService clienteService;
	
	private Cliente cliente;
	
	public ClienteServiceTest() {
		IClienteDAO dao = new ClienteDaoMock();
		clienteService = new ClienteService(dao);
	}
	
	@BeforeEach
	public void init() {
		cliente = new Cliente();
		cliente.setCpf(12312312312L);
		cliente.setNome("Fabio Peretti");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End. Bela Vista");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(1199999999L);
		
	}
	
	@Test
	public void pesquisarCliente() throws DAOException {
		Cliente clienteConsultado = clienteService.buscarPorCPF(cliente.getCpf());
		Assertions.assertNotNull(clienteConsultado);
	}
	
	@Test
	public void salvarCliente() throws TipoChaveNaoEncontradaException, DAOException {
		Boolean retorno = clienteService.cadastrar(cliente);
		
		Assertions.assertTrue(retorno);
	}
	
	@Test
	public void excluirCliente() throws DAOException {
		clienteService.excluir(cliente.getCpf());
	}
	
	@Test
	public void alterarCliente() throws TipoChaveNaoEncontradaException, DAOException {
		cliente.setNome("Fabio Peretti");
		clienteService.alterar(cliente);
		
		Assertions.assertEquals("Fabio Peretti", cliente.getNome());
	}
}
