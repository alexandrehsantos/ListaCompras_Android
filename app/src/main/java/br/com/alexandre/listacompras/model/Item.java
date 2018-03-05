package br.com.alexandre.listacompras.model;

/**
 * Created by alexandre on 04/03/18.
 */

public class Item {



    private String uId;
    private String descricao;
    private Double quantidade;





    public String getDescricao() {
        return descricao;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
