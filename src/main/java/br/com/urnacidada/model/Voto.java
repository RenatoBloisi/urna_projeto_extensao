package br.com.urnacidada.model;

public class Voto {

    public enum TipoVoto {
        VALIDO,
        BRANCO,
        NULO
    };

    private String numero;
    private Cargo cargo;
    private TipoVoto tipoVoto; //Válido, Branco ou Nulo

    // Construtor
    public Voto(Cargo cargo, String numero, TipoVoto tipoVoto) {
        this.cargo = cargo;
        this.numero = numero;
        this.tipoVoto = tipoVoto;
    }

    // Getters
    public String getNumero() {
        return numero;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public TipoVoto getTipoVoto() {
        return tipoVoto;
    }

    // Setters
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public void setTipoVoto(TipoVoto tipoVoto) {
        this.tipoVoto = tipoVoto;
    }

    @Override
    public String toString() {
        return "Voto [Cargo=" + cargo + ", Número=" + numero + ", Tipo=" + tipoVoto + "]";
    }
}