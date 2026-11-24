package br.com.urnacidada.model;

public enum Cargo {
    DEPUTADO_FEDERAL(4, "Deputado Federal"),
    DEPUTADO_ESTADUAL(5, "Deputado Estadual"),
    PRESIDENTE(2, "Presidente da República");

    private final int qtdDigitos;
    private final String descricao;

    Cargo(int qtdDigito, String descricao) {
        this.qtdDigitos = qtdDigito;
        this.descricao = descricao;
    }

    public int getQtdDigitos() {
        return qtdDigitos;
    }

    public String getDescricao() {
        return descricao;
    }
}
