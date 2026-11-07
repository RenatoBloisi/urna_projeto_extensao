package br.com.urnacidada.model;

public class Voto {
    private String numero;
    private String cargo;
    private String tipoVoto; //Válido, Branco ou Nulo

    // Construtor
    public Voto(String numero, String cargo, String tipoVoto) {
        this.numero = numero;
        this.cargo = cargo;
        this.tipoVoto = tipoVoto;
    }

    // Getters
    public String getNumero() {
        return numero;
    }

    public String getCargo() {
        return cargo;
    }

    public String getTipoVoto() {
        return tipoVoto;
    }

    // Setters
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setTipoVoto(String tipoVoto) {
        this.tipoVoto = tipoVoto;
    }

    @Override
    public String toString() {
        return "Voto [Cargo=" + cargo + ", Número=" + numero + ", Tipo=" + tipoVoto + "]";
    }
}