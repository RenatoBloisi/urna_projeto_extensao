package br.com.urnacidada.model;

public class Candidato {
    private String nome;
    private String numero;
    private String partido;
    private String cargo;

    // Construtor
    public Candidato(String nome, String numero, String partido, String cargo) {
        this.nome = nome;
        this.numero = numero;
        this.partido = partido;
        this.cargo = cargo;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getNumero() {
        return numero;
    }

    public String getPartido() {
        return partido;
    }

    public String getCargo() {
        return cargo;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    // Método toString()
    @Override
    public String toString() {
        return "Candidato [Nome=" + nome + ", Número=" + numero + ", Partido=" + partido + ", Cargo=" + cargo + "]";
    }
}