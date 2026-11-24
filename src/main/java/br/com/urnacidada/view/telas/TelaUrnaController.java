package br.com.urnacidada.view.telas;

import br.com.urnacidada.controller.UrnaController;
import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.model.Cargo;
import br.com.urnacidada.model.Voto;
import br.com.urnacidada.view.apresentacao.Mensagens;
import br.com.urnacidada.view.apresentacao.PainelApresentacao;
import br.com.urnacidada.view.apresentacao.Responsivo;
import br.com.urnacidada.view.apresentacao.TipoMensagem;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Controlador da URNA PRINCIPAL ({@code TelaUrna.fxml}) — parte da Pessoa 4:
 * janela da urna, teclado numérico, botões BRANCO / CORRIGE / CONFIRMA e área de digitação.
 *
 * <p>Esta classe só faz a PONTE entre os botões e a lógica que já existe:</p>
 * <ul>
 *   <li>cada tecla chama o método correspondente do {@link UrnaController} (Pessoa 2);</li>
 *   <li>depois pergunta ao {@code UrnaController} o que mostrar e desenha o resultado;</li>
 *   <li>a ficha do candidato, as mensagens e a tela FIM são exibidas pelo
 *       {@link PainelApresentacao} (Pessoa 5), que fica POR CIMA do visor.</li>
 * </ul>
 *
 * <p>NÃO tem regra de votação (quem decide validade, nulo, branco, fim etc. é o
 * {@code UrnaController}), NÃO acessa o Firebase e NÃO grava votos. Ao terminar, avisa por
 * {@link #setAoVotacaoEncerrada(Consumer)}, que é onde os votos devem ser gravados.</p>
 *
 * <p>Teclado físico: dígitos 0-9 digitam, ENTER confirma, BACKSPACE/DELETE/ESC corrigem, B = branco.</p>
 */
public class TelaUrnaController {

    private static final double LARGURA_BASE = 1100;
    private static final double ALTURA_BASE = 660;
    private static final Duration TEMPO_VOTO_CONFIRMADO = Duration.seconds(1.2);
    private static final Duration TEMPO_AVISO = Duration.seconds(2.0);

    @FXML private BorderPane raiz;
    @FXML private StackPane visor;
    @FXML private VBox boxDigitacao;
    @FXML private Label lblEtapa;
    @FXML private Label lblCargoAtual;
    @FXML private HBox boxDigitos;
    @FXML private Label lblBranco;
    @FXML private Label lblDica;
    @FXML private Label lblRodape;

    private PainelApresentacao painel;
    private UrnaController urna;
    private Consumer<List<Voto>> aoVotacaoEncerrada;
    private Runnable aoReiniciar;

    private boolean entradaBloqueada;
    private PauseTransition desbloqueio;

    @FXML
    private void initialize() {
        Responsivo.escalar(raiz, LARGURA_BASE, ALTURA_BASE);

        // Camadas da Pessoa 5 (ficha, mensagens, FIM) por cima da área de digitação.
        painel = new PainelApresentacao();
        visor.getChildren().add(painel.getRaiz());

        // Teclado físico. Os botões da tela não recebem foco, então a raiz recebe as teclas.
        raiz.setOnKeyPressed(this::aoPressionarTecla);
        raiz.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> raiz.requestFocus());
        raiz.sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                Platform.runLater(raiz::requestFocus);
            }
        });
    }

    // ------------------------------------------------------------------
    // API de integração
    // ------------------------------------------------------------------

    /**
     * Liga a urna à lógica de votação e (re)inicia a sessão na tela. Deve ser chamado
     * antes de a tela ser usada. Chamar de novo com um novo {@code UrnaController}
     * começa uma nova votação.
     */
    public void setUrna(UrnaController urna) {
        this.urna = Objects.requireNonNull(urna, "urna");
        this.entradaBloqueada = false;
        if (desbloqueio != null) {
            desbloqueio.stop();
        }
        painel.reiniciar();
        atualizarVisor();
        Platform.runLater(raiz::requestFocus);
    }

    /**
     * Chamado quando o último voto é confirmado (junto com a exibição da tela FIM).
     * Recebe os votos registrados. O aplicativo usa este callback para gravá-los
     * (veja {@code UrnaCidadaApp}); a gravação deve sempre ocorrer em thread de fundo,
     * nunca na thread do JavaFX.
     */
    public void setAoVotacaoEncerrada(Consumer<List<Voto>> aoVotacaoEncerrada) {
        this.aoVotacaoEncerrada = aoVotacaoEncerrada;
    }

    /** Ação do botão "VOLTAR AO INÍCIO" da tela final (null = botão oculto). */
    public void setAoReiniciar(Runnable aoReiniciar) {
        this.aoReiniciar = aoReiniciar;
    }

    /** Acesso ao painel de apresentação (Pessoa 5), caso se precise exibir algo extra. */
    public PainelApresentacao getPainel() {
        return painel;
    }

    // ------------------------------------------------------------------
    // Ações da tela (ligadas no FXML)
    // ------------------------------------------------------------------

    @FXML
    private void aoClicarTecla(ActionEvent evento) {
        Button botao = (Button) evento.getSource();
        digitar(botao.getText().charAt(0));
    }

    @FXML
    private void aoClicarBranco() {
        votarEmBranco();
    }

    @FXML
    private void aoClicarCorrige() {
        corrigir();
    }

    @FXML
    private void aoClicarConfirma() {
        confirmar();
    }

    // ------------------------------------------------------------------
    // Operações (delegam para o UrnaController)
    // ------------------------------------------------------------------

    private boolean podeInteragir() {
        return urna != null && !urna.isVotacaoEncerrada() && !entradaBloqueada;
    }

    private void digitar(char digito) {
        if (!podeInteragir() || urna.isVotoEmBranco()) {
            return; // em branco, o UrnaController ignora dígitos; a dica na tela explica
        }
        int antes = urna.getNumeroDigitado().length();
        urna.digitarNumero(digito);
        if (urna.getNumeroDigitado().length() == antes) {
            return; // número já completo: tecla ignorada
        }
        painel.ocultarMensagem();
        atualizarVisor();

        Cargo cargo = urna.getCargoAtual();
        boolean completo = urna.getNumeroDigitado().length() == cargo.getQtdDigitos();
        if (completo && urna.buscarCandidatoAtual() == null) {
            painel.exibirMensagemTemporaria(TipoMensagem.AVISO,
                    Mensagens.CANDIDATO_NAO_ENCONTRADO, TEMPO_AVISO);
        }
    }

    private void corrigir() {
        if (!podeInteragir()) {
            return;
        }
        urna.corrigir();
        painel.ocultarMensagem();
        atualizarVisor();
    }

    private void votarEmBranco() {
        if (!podeInteragir()) {
            return;
        }
        urna.votarEmBranco();
        painel.ocultarMensagem();
        atualizarVisor();
    }

    private void confirmar() {
        if (!podeInteragir()) {
            return;
        }
        painel.ocultarMensagem();

        boolean confirmado = urna.confirmarVoto();
        if (!confirmado) {
            // O UrnaController recusa quando o número está incompleto.
            painel.exibirMensagemTemporaria(TipoMensagem.AVISO, Mensagens.NUMERO_INCOMPLETO, TEMPO_AVISO);
            return;
        }

        painel.ocultarCandidato();
        if (urna.isVotacaoEncerrada()) {
            // O sinal de fim vem SEMPRE da lógica (Pessoa 2): isVotacaoEncerrada().
            painel.exibirFim(aoReiniciar);
            if (aoVotacaoEncerrada != null) {
                aoVotacaoEncerrada.accept(urna.getVotosRegistrados());
            }
        } else {
            atualizarVisor(); // já mostra o próximo cargo por trás da mensagem
            bloquearEntrada(TEMPO_VOTO_CONFIRMADO);
            painel.exibirMensagemTemporaria(TipoMensagem.SUCESSO, Mensagens.VOTO_CONFIRMADO,
                    TEMPO_VOTO_CONFIRMADO);
        }
    }

    /** Evita digitar sem querer enquanto a mensagem "Voto confirmado." está na tela. */
    private void bloquearEntrada(Duration duracao) {
        entradaBloqueada = true;
        if (desbloqueio != null) {
            desbloqueio.stop();
        }
        desbloqueio = new PauseTransition(duracao);
        desbloqueio.setOnFinished(e -> entradaBloqueada = false);
        desbloqueio.play();
    }

    // ------------------------------------------------------------------
    // Desenho do visor (lê o estado do UrnaController; não guarda estado próprio)
    // ------------------------------------------------------------------

    private void atualizarVisor() {
        Cargo cargo = urna.getCargoAtual();
        if (cargo == null) {
            return; // votação encerrada: quem cuida é a tela FIM
        }
        String numero = urna.getNumeroDigitado();
        boolean branco = urna.isVotoEmBranco();
        int total = cargo.getQtdDigitos();

        // O número da etapa usa a ordem de declaração do enum Cargo (federal, estadual,
        // presidente), que hoje coincide com a ordem de votação do UrnaController.
        lblEtapa.setText("ETAPA " + (cargo.ordinal() + 1) + " DE " + Cargo.values().length);
        lblCargoAtual.setText(cargo.getDescricao());
        montarCaixas(total, numero, branco);
        alternar(lblBranco, branco);

        boolean completo = !branco && numero.length() == total;
        Candidato candidato = completo ? urna.buscarCandidatoAtual() : null;

        lblDica.getStyleClass().remove("dica-erro");
        if (branco) {
            lblDica.setText("Voto em branco. Para digitar um número, aperte CORRIGE.");
        } else if (candidato != null) {
            lblDica.setText("Confira os dados do candidato e aperte CONFIRMA.");
        } else if (completo) {
            lblDica.setText(Mensagens.NUMERO_SEM_CANDIDATO);
            lblDica.getStyleClass().add("dica-erro");
        } else if (numero.isEmpty()) {
            lblDica.setText("Digite o número do candidato ou aperte BRANCO.");
        } else {
            int faltam = total - numero.length();
            lblDica.setText(faltam == 1 ? "Falta 1 dígito." : "Faltam " + faltam + " dígitos.");
        }

        if (candidato != null) {
            painel.exibirCandidato(candidato);
        } else {
            painel.ocultarCandidato();
        }
    }

    /** Uma caixinha por dígito do cargo; a próxima a ser preenchida fica destacada. */
    private void montarCaixas(int total, String numero, boolean branco) {
        boxDigitos.getChildren().clear();
        for (int i = 0; i < total; i++) {
            Label caixa = new Label(i < numero.length() ? String.valueOf(numero.charAt(i)) : "");
            caixa.getStyleClass().add("digito-caixa");
            if (!branco && i == numero.length()) {
                caixa.getStyleClass().add("digito-ativo");
            }
            boxDigitos.getChildren().add(caixa);
        }
    }

    // ------------------------------------------------------------------
    // Teclado físico
    // ------------------------------------------------------------------

    private void aoPressionarTecla(KeyEvent evento) {
        if (urna == null) {
            return;
        }
        KeyCode codigo = evento.getCode();
        String texto = evento.getText();
        if (codigo == KeyCode.ENTER) {
            confirmar();
            evento.consume();
        } else if (codigo == KeyCode.BACK_SPACE || codigo == KeyCode.DELETE || codigo == KeyCode.ESCAPE) {
            corrigir();
            evento.consume();
        } else if (codigo == KeyCode.B) {
            votarEmBranco();
            evento.consume();
        } else if (texto != null && texto.length() == 1
                && texto.charAt(0) >= '0' && texto.charAt(0) <= '9') {
            digitar(texto.charAt(0));
            evento.consume();
        }
    }

    private static void alternar(Node no, boolean visivel) {
        no.setVisible(visivel);
        no.setManaged(visivel);
    }
}
