package br.com.urnacidada.view.telas;

import br.com.urnacidada.view.apresentacao.Responsivo;
import br.com.urnacidada.view.apresentacao.TipoMensagem;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controlador da TELA 3 — Mensagens e avisos ({@code Mensagem.fxml}).
 *
 * <p>É uma camada (overlay) reutilizável: o dono da tela a coloca dentro de um
 * {@code StackPane} e chama {@code exibir...}. Não abre janela nova. Os textos são
 * sempre recebidos por parâmetro (ver {@code Mensagens} para os textos padronizados).</p>
 *
 * <p>Apenas APRESENTA. Validar número, registrar voto ou decidir quando mostrar cada
 * mensagem NÃO é desta classe. Os botões só disparam os {@link Runnable} recebidos.</p>
 */
public class MensagemController {

    private static final double LARGURA_BASE = 760;
    private static final double ALTURA_BASE = 500;

    @FXML private StackPane raiz;
    @FXML private VBox cartao;
    @FXML private Label lblIcone;
    @FXML private Label lblTitulo;
    @FXML private Label lblTexto;
    @FXML private HBox boxBotoes;
    @FXML private Button btnPrimario;
    @FXML private Button btnSecundario;

    private Runnable acaoPrimaria;
    private Runnable acaoSecundaria;
    private PauseTransition temporizador;

    @FXML
    private void initialize() {
        Responsivo.escalar(raiz, LARGURA_BASE, ALTURA_BASE);
        esconder(); // no Scene Builder fica visível para edição; em execução começa oculta
    }

    // ------------------------------------------------------------------
    // API de integração
    // ------------------------------------------------------------------

    /** Mensagem simples, sem botões. Fica na tela até {@link #ocultar()} ser chamado. */
    public void exibir(TipoMensagem tipo, String texto) {
        exibir(tipo, tipo.getTituloPadrao(), texto);
    }

    /** Mensagem simples com título personalizado (título vazio ou nulo = sem título). */
    public void exibir(TipoMensagem tipo, String titulo, String texto) {
        apresentar(tipo, titulo, texto, null, null, null, null);
    }

    /**
     * Mensagem com um botão. Ao clicar, a mensagem some e {@code aoClicar} é executado
     * (pode ser {@code null}).
     */
    public void exibirComBotao(TipoMensagem tipo, String texto, String rotuloBotao, Runnable aoClicar) {
        apresentar(tipo, tipo.getTituloPadrao(), texto, rotuloBotao, aoClicar, null, null);
    }

    /**
     * Pergunta com dois botões (ex.: "Deseja corrigir sua escolha?" — SIM / NÃO).
     * A mensagem some ao clicar e a ação correspondente é executada (pode ser {@code null}).
     */
    public void exibirConfirmacao(String texto,
                                  String rotuloSim, Runnable aoSim,
                                  String rotuloNao, Runnable aoNao) {
        TipoMensagem tipo = TipoMensagem.PERGUNTA;
        apresentar(tipo, tipo.getTituloPadrao(), texto, rotuloSim, aoSim, rotuloNao, aoNao);
    }

    /**
     * Mensagem de qualquer tipo com dois botões (ex.: erro com "TENTAR NOVAMENTE" / "VOLTAR").
     * A mensagem some ao clicar e a ação correspondente é executada (pode ser {@code null}).
     */
    public void exibirComBotoes(TipoMensagem tipo, String texto,
                                String rotuloPrimario, Runnable aoPrimario,
                                String rotuloSecundario, Runnable aoSecundario) {
        apresentar(tipo, tipo.getTituloPadrao(), texto, rotuloPrimario, aoPrimario,
                rotuloSecundario, aoSecundario);
    }

    /** Mensagem que desaparece sozinha depois de {@code duracao}. */
    public void exibirTemporaria(TipoMensagem tipo, String texto, Duration duracao) {
        exibir(tipo, texto);
        temporizador = new PauseTransition(duracao);
        temporizador.setOnFinished(e -> ocultar());
        temporizador.play();
    }

    /** Esconde a mensagem e descarta as ações pendentes. */
    public void ocultar() {
        esconder();
    }

    public boolean isExibindo() {
        return raiz.isVisible();
    }

    // ------------------------------------------------------------------
    // Ações dos botões (ligadas no FXML)
    // ------------------------------------------------------------------

    @FXML
    private void aoClicarPrimario() {
        Runnable acao = acaoPrimaria;
        esconder();
        if (acao != null) {
            acao.run();
        }
    }

    @FXML
    private void aoClicarSecundario() {
        Runnable acao = acaoSecundaria;
        esconder();
        if (acao != null) {
            acao.run();
        }
    }

    // ------------------------------------------------------------------
    // Internos
    // ------------------------------------------------------------------

    private void apresentar(TipoMensagem tipo, String titulo, String texto,
                            String rotuloPrimario, Runnable primaria,
                            String rotuloSecundario, Runnable secundaria) {
        pararTemporizador();

        // Aparência por tipo: troca só a classe CSS do cartão.
        for (TipoMensagem t : TipoMensagem.values()) {
            cartao.getStyleClass().remove(t.getClasseCss());
        }
        cartao.getStyleClass().add(tipo.getClasseCss());
        lblIcone.setText(tipo.getIcone());

        boolean temTitulo = titulo != null && !titulo.isBlank();
        lblTitulo.setText(temTitulo ? titulo : "");
        alternar(lblTitulo, temTitulo);
        lblTexto.setText(texto == null ? "" : texto);

        acaoPrimaria = primaria;
        acaoSecundaria = secundaria;
        boolean temPrimario = rotuloPrimario != null;
        boolean temSecundario = rotuloSecundario != null;
        btnPrimario.setText(temPrimario ? rotuloPrimario : "");
        btnSecundario.setText(temSecundario ? rotuloSecundario : "");
        alternar(btnPrimario, temPrimario);
        alternar(btnSecundario, temSecundario);
        alternar(boxBotoes, temPrimario || temSecundario);

        alternar(raiz, true);
        if (temPrimario) {
            Platform.runLater(btnPrimario::requestFocus);
        }
    }

    private void esconder() {
        pararTemporizador();
        acaoPrimaria = null;
        acaoSecundaria = null;
        alternar(raiz, false);
    }

    private void pararTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
    }

    /** Mostra/esconde um nó sem deixar espaço vazio no layout. */
    private static void alternar(javafx.scene.Node no, boolean visivel) {
        no.setVisible(visivel);
        no.setManaged(visivel);
    }
}
