package br.com.fabioperettig.dao;

import java.util.Collection;

import br.com.fabioperettig.domain.Produto;
import br.com.fabioperettig.exceptions.TipoChaveNaoEncontradaException;

public class ProdutoDaoMock implements IProdutoDAO {
    @Override
    public Boolean cadastrar(Produto produto, Integer quantidadeInicial) {
		return true;
	}

	@Override
	public Boolean cadastrar(Produto entity) throws TipoChaveNaoEncontradaException {
		return true;
	}

	@Override
	public void excluir(String valor) {

	}

	@Override
	public void alterar(Produto entity) throws TipoChaveNaoEncontradaException {
		
	}

	@Override
	public Produto consultar(String valor) {
		Produto produto = new Produto();
		produto.setCodigo(valor);
		return produto;
	}

	@Override
	public Collection<Produto> buscarTodos() {
		return null;
	}

}
