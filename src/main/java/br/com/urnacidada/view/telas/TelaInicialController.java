package br.com.urnacidada.view.telas;

import br.com.urnacidada.model.Cargo;
import br.com.urnacidada.view.apresentacao.Responsivo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Controlador da TELA 1 — Boas-vindas ({@code TelaInicial.fxml}).
 *
 * <p>Somente APRESENTAÇÃO: esta classe não conhece a urna nem o Firebase. Quando o
 * eleitor clica em "INICIAR VOTAÇÃO", ela apenas avisa quem estiver ouvindo, por meio
 * de {@link #setAoIniciar(Runnable)}. O que acontece depois (abrir a urna principal,
 * criar o UrnaController etc.) é responsabilidade de quem integra (Pessoas 4 e 6).</p>
 */
public class TelaInicialController {

    private static final double LARGURA_BASE = 1100;
    private static final double ALTURA_BASE = 700;

    @FXML private BorderPane raiz;
    @FXML private Label lblBoasVindas;
    @FXML private Label lblAviso;
    @FXML private Label lblCargo1;
    @FXML private Label lblCargo2;
    @FXML private Label lblCargo3;
    @FXML private Label lblDigitos1;
    @FXML private Label lblDigitos2;
    @FXML private Label lblDigitos3;

    private Runnable aoIniciar;

    @FXML
    private void initialize() {
        Responsivo.escalar(raiz, LARGURA_BASE, ALTURA_BASE);
        preencherCargos();
    }

    /**
     * Define o que acontece ao clicar em "INICIAR VOTAÇÃO".
     * Se nada for definido, o clique não faz nada.
     */
    public void setAoIniciar(Runnable aoIniciar) {
        this.aoIniciar = aoIniciar;
    }

    /** Permite trocar a mensagem de boas-vindas sem editar o FXML. */
    public void setTextoBoasVindas(String texto) {
        lblBoasVindas.setText(texto);
    }

    /** Permite trocar o aviso de "candidatos fictícios / votação educativa". */
    public void setTextoAviso(String texto) {
        lblAviso.setText(texto);
    }

    @FXML
    private void aoClicarIniciar() {
        if (aoIniciar != null) {
            aoIniciar.run();
        }
    }

    /**
     * Mostra os cargos usando o enum {@link Cargo} real do projeto (descrição e número
     * de dígitos). Assim, se a Pessoa 1 mudar um texto ou a quantidade de dígitos, esta
     * tela acompanha automaticamente.
     *
     * <p>ATENÇÃO: usa a ordem de declaração do enum (federal, estadual, presidente), que hoje
     * coincide com a ordem de votação definida em UrnaController. Se uma das duas mudar,
     * ajuste aqui.</p>
     */
    private void preencherCargos() {
        Label[] nomes = {lblCargo1, lblCargo2, lblCargo3};
        Label[] digitos = {lblDigitos1, lblDigitos2, lblDigitos3};
        Cargo[] cargos = Cargo.values();
        for (int i = 0; i < nomes.length && i < cargos.length; i++) {
            nomes[i].setText(cargos[i].getDescricao());
            digitos[i].setText(cargos[i].getQtdDigitos() + " dígitos");
        }
    }
}
