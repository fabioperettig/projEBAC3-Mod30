package br.com.fabioperettig.services;

import br.com.fabioperettig.domain.Cliente;
import br.com.fabioperettig.exceptions.DAOException;
import br.com.fabioperettig.services.generic.IGenericService;

public interface IClienteService extends IGenericService<Cliente, Long> {
	Cliente buscarPorCPF(Long cpf) throws DAOException;
}
