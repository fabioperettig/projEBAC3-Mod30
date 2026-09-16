package br.com.fabioperettig;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.com.fabioperettig.dao.ClienteDAO;
import br.com.fabioperettig.dao.IClienteDAO;
import br.com.fabioperettig.domain.Cliente;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.exceptions.MaisDeUmRegistroException;
import br.com.fabioperettig.exceptions.TableException;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;

@org.junit.jupiter.api.extension.ExtendWith(BancoTeste.class)
public class ClienteDAOTest {
	
	private IClienteDAO clienteDao;

	public ClienteDAOTest() {
		clienteDao = new ClienteDAO();
	}
	
	@AfterEach
	public void end() throws DAOException {
		Collection<Cliente> list = clienteDao.buscarTodos();
		list.forEach(cli -> {
			try {
				clienteDao.excluir(cli.getCpf());
			} catch (DAOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Test
	public void pesquisarCliente() throws
			MaisDeUmRegistroException,
			TableException,
			TipoChaveNaoEncontradaException,
			DAOException
	{

		Cliente cliente = new Cliente();
		cliente.setCpf(12312312312L);
		cliente.setNome("Fabio");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End. Bela Vista");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(9999999999L);
		clienteDao.cadastrar(cliente);
		
		Cliente clienteConsultado = clienteDao.consultar(cliente.getCpf());
		Assertions.assertNotNull(clienteConsultado);
		
		clienteDao.excluir(cliente.getCpf());
	}
	
	@Test
	public void salvarCliente() throws
			TipoChaveNaoEncontradaException,
			MaisDeUmRegistroException,
			TableException,
			DAOException
	{

		Cliente cliente = new Cliente();
		cliente.setCpf(56565656565L);
		cliente.setNome("Fabio");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End. Bela Vista");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(9999999999L);
		Boolean retorno = clienteDao.cadastrar(cliente);
		Assertions.assertTrue(retorno);
		
		Cliente clienteConsultado = clienteDao.consultar(cliente.getCpf());
		Assertions.assertNotNull(clienteConsultado);
		
		clienteDao.excluir(cliente.getCpf());
	}
	
	@Test
	public void excluirCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(56565656565L);
		cliente.setNome("Fabio");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End. Bela Vista");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(9999999999L);
		Boolean retorno = clienteDao.cadastrar(cliente);
		Assertions.assertTrue(retorno);
		
		Cliente clienteConsultado = clienteDao.consultar(cliente.getCpf());
		Assertions.assertNotNull(clienteConsultado);
		
		clienteDao.excluir(cliente.getCpf());
		clienteConsultado = clienteDao.consultar(cliente.getCpf());
		Assertions.assertNull(clienteConsultado);
	}
	
	@Test
	public void alterarCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(56565656565L);
		cliente.setNome("Fabio");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End. Bela Vista");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(9999999999L);
		Boolean retorno = clienteDao.cadastrar(cliente);
		Assertions.assertTrue(retorno);
		
		Cliente clienteConsultado = clienteDao.consultar(cliente.getCpf());
		Assertions.assertNotNull(clienteConsultado);
		
		clienteConsultado.setNome("Fabio Peretti");
		clienteDao.alterar(clienteConsultado);
		
		Cliente clienteAlterado = clienteDao.consultar(clienteConsultado.getCpf());
		Assertions.assertNotNull(clienteAlterado);
		Assertions.assertEquals("Fabio Peretti", clienteAlterado.getNome());
		
		clienteDao.excluir(cliente.getCpf());
		clienteConsultado = clienteDao.consultar(cliente.getCpf());
		Assertions.assertNull(clienteConsultado);
	}
	
	@Test
	public void buscarTodos() throws TipoChaveNaoEncontradaException, DAOException {
		Cliente cliente = new Cliente();
		cliente.setCpf(56565656565L);
		cliente.setNome("Fabio");
		cliente.setCidade("São Paulo");
		cliente.setEnd("End. Bela Vista");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTel(9999999999L);
		Boolean retorno = clienteDao.cadastrar(cliente);
		Assertions.assertTrue(retorno);
		
		Cliente cliente1 = new Cliente();
		cliente1.setCpf(56565656569L);
		cliente1.setNome("Fabio");
		cliente1.setCidade("São Paulo");
		cliente1.setEnd("End. Bela Vista");
		cliente1.setEstado("SP");
		cliente1.setNumero(10);
		cliente1.setTel(9999999999L);
		Boolean retorno1 = clienteDao.cadastrar(cliente1);
		Assertions.assertTrue(retorno1);
		
		Collection<Cliente> list = clienteDao.buscarTodos();
		assertTrue(list != null);
		assertTrue(list.size() == 2);
		
		list.forEach(cli -> {
			try {
				clienteDao.excluir(cli.getCpf());
			} catch (DAOException e) {
				e.printStackTrace();
			}
		});
		
		Collection<Cliente> list1 = clienteDao.buscarTodos();
		assertTrue(list1 != null);
		assertTrue(list1.size() == 0);
	}
}
