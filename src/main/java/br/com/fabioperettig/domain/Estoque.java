package br.com.fabioperettig.domain;

import anotacao.ColunaTabela;
import anotacao.Tabela;
import anotacao.TipoChave;
import br.com.fabioperettig.dao.Persistente;

@Tabela("TB_ESTOQUE")
public class Estoque implements Persistente {
    @ColunaTabela(dbName = "id", setJavaName = "setId")
    private Long id;
    @TipoChave("getIdProduto")
    @ColunaTabela(dbName = "id_produto_fk", setJavaName = "setIdProduto")
    private Long idProduto;
    @ColunaTabela(dbName = "quantidade", setJavaName = "setQuantidade")
    private Integer quantidade;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
