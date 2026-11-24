package br.com.urnacidada.view.telas;

import br.com.urnacidada.view.apresentacao.Responsivo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Controlador da TELA 4 — Encerramento ({@code TelaFinal.fxml}).
 *
 * <p>Esta classe NÃO decide quando a votação termina. Ela só é exibida quando quem
 * integra mandar (hoje, o sinal real é {@code UrnaController.isVotacaoEncerrada()},
 * da Pessoa 2). Nenhuma regra de encerramento é implementada aqui, para não conflitar
 * com a lógica da urna.</p>
 *
 * <p>O botão "VOLTAR AO INÍCIO" só aparece se alguém definir a ação em
 * {@link #setAoReiniciar(Runnable)} (ou seja, apenas "caso a lógica principal permita").</p>
 */
public class TelaFinalController {

    private static final double LARGURA_BASE = 760;
    private static final double ALTURA_BASE = 500;

    @FXML private BorderPane raiz;
    @FXML private Label lblMensagem;
    @FXML private Label lblSimulacao;
    @FXML private Button btnReiniciar;

    private Runnable aoReiniciar;

    @FXML
    private void initialize() {
        Responsivo.escalar(raiz, LARGURA_BASE, ALTURA_BASE);
        mostrarBotao(false); // sem ação definida, não há para onde voltar
    }

    /**
     * Define a ação de "VOLTAR AO INÍCIO". Com {@code null}, o botão fica oculto.
     */
    public void setAoReiniciar(Runnable aoReiniciar) {
        this.aoReiniciar = aoReiniciar;
        mostrarBotao(aoReiniciar != null);
    }

    /** Troca a mensagem principal (padrão: "Votação concluída."). */
    public void setMensagem(String texto) {
        lblMensagem.setText(texto);
    }

    /** Troca o aviso de simulação educativa. */
    public void setAvisoSimulacao(String texto) {
        lblSimulacao.setText(texto);
    }

    @FXML
    private void aoClicarReiniciar() {
        if (aoReiniciar != null) {
            aoReiniciar.run();
        }
    }

    private void mostrarBotao(boolean visivel) {
        btnReiniciar.setVisible(visivel);
        btnReiniciar.setManaged(visivel);
    }
}
