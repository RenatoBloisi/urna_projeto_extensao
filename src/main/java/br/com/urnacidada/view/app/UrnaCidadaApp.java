package br.com.urnacidada.view.app;

import br.com.urnacidada.controller.UrnaController;
import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.model.Voto;
import br.com.urnacidada.view.apresentacao.Mensagens;
import br.com.urnacidada.view.apresentacao.PainelApresentacao;
import br.com.urnacidada.view.apresentacao.Recursos;
import br.com.urnacidada.view.apresentacao.Telas;
import br.com.urnacidada.view.apresentacao.Telas.Tela;
import br.com.urnacidada.view.apresentacao.TipoMensagem;
import br.com.urnacidada.view.integracao.ServicosDaUrna;
import br.com.urnacidada.view.integracao.ServicosFirebase;
import br.com.urnacidada.view.telas.TelaInicialController;
import br.com.urnacidada.view.telas.TelaUrnaController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * Aplicação JavaFX da Urna Cidadã. Controla a navegação:
 *
 * <pre>Tela Inicial → Urna (digitação, candidatos, mensagens) → Tela FIM → Tela Inicial</pre>
 *
 * <p>A lógica de votação é do {@link UrnaController}; a persistência é feita pelos
 * {@link ServicosDaUrna} (candidatos e votos), sempre em thread de fundo, para a tela nunca
 * congelar. Se carregar ou gravar falhar, o eleitor vê uma mensagem com a opção de tentar de novo.</p>
 *
 * <p>Atalho: <b>F11</b> alterna tela cheia.</p>
 */
public class UrnaCidadaApp extends Application {

    private static final double LARGURA_INICIAL = 1180;
    private static final double ALTURA_INICIAL = 740;

    private final StackPane palco = new StackPane();
    private ServicosDaUrna servicos;

    /**
     * Escolhe de onde vêm os candidatos e para onde vão os votos.
     *
     * <p>Padrão: <b>Firebase</b> (Cloud Firestore). Para treinar sem internet/sem chave, abra o
     * programa com {@code -Durna.modo=local} (ou a variável de ambiente {@code URNA_MODO=local}):
     * usa os 16 candidatos fixos e guarda os votos só em memória. Configuração do Firebase em
     * {@code docs/CONFIGURAR_FIREBASE.md}.</p>
     */
    private ServicosDaUrna criarServicos() {
        String modo = System.getProperty("urna.modo", System.getenv("URNA_MODO"));
        if ("local".equalsIgnoreCase(modo)) {
            System.out.println("[Urna Cidadã] Modo LOCAL (sem Firebase).");
            return ServicosDaUrna.locais();
        }
        return new ServicosFirebase().comoServicos();
    }

    @Override
    public void start(Stage stage) {
        servicos = criarServicos();

        Scene cena = new Scene(palco, LARGURA_INICIAL, ALTURA_INICIAL);
        cena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            if (evento.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                evento.consume();
            }
        });

        stage.setTitle("Urna Cidadã — Simulação Educativa do Voto Eletrônico");
        stage.setMinWidth(900);
        stage.setMinHeight(640);
        // ESC é a tecla CORRIGE do teclado do computador; só o F11 sai da tela cheia.
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint("Pressione F11 para sair da tela cheia");
        stage.setScene(cena);
        stage.show();

        mostrarTelaInicial();
    }

    // ------------------------------------------------------------------
    // Navegação
    // ------------------------------------------------------------------

    private void mostrarTelaInicial() {
        Tela<TelaInicialController> tela = Telas.carregar(Recursos.FXML_TELA_INICIAL);
        tela.controlador().setAoIniciar(this::iniciarVotacao);
        palco.getChildren().setAll(tela.raiz());
    }

    private void iniciarVotacao() {
        Tela<TelaUrnaController> tela = Telas.carregar(Recursos.FXML_TELA_URNA);
        TelaUrnaController urna = tela.controlador();
        urna.setAoReiniciar(this::mostrarTelaInicial);
        urna.setAoVotacaoEncerrada(votos -> gravarVotos(urna, votos));
        palco.getChildren().setAll(tela.raiz());

        carregarCandidatos(urna);
    }

    // ------------------------------------------------------------------
    // Serviços externos (sempre fora da thread do JavaFX)
    // ------------------------------------------------------------------

    private void carregarCandidatos(TelaUrnaController urna) {
        PainelApresentacao painel = urna.getPainel();
        painel.exibirMensagem(TipoMensagem.INFORMACAO, Mensagens.CARREGANDO_CANDIDATOS);

        Task<List<Candidato>> tarefa = new Task<>() {
            @Override
            protected List<Candidato> call() throws Exception {
                return servicos.candidatos().carregar();
            }
        };
        tarefa.setOnSucceeded(e -> {
            List<Candidato> candidatos = tarefa.getValue();
            if (candidatos == null || candidatos.isEmpty()) {
                System.err.println("[Urna Cidadã] A fonte de candidatos não devolveu nenhum candidato.");
                mostrarErroAoCarregar(urna);
                return;
            }
            urna.setUrna(new UrnaController(candidatos)); // também esconde a mensagem "Carregando..."
        });
        tarefa.setOnFailed(e -> {
            registrarErro("carregar candidatos", tarefa.getException());
            mostrarErroAoCarregar(urna);
        });
        executar(tarefa);
    }

    private void mostrarErroAoCarregar(TelaUrnaController urna) {
        urna.getPainel().exibirMensagemComBotoes(TipoMensagem.ERRO,
                Mensagens.ERRO_CARREGAR_CANDIDATO,
                "TENTAR NOVAMENTE", () -> carregarCandidatos(urna),
                "VOLTAR AO INÍCIO", this::mostrarTelaInicial);
    }

    private void gravarVotos(TelaUrnaController urna, List<Voto> votos) {
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() throws Exception {
                servicos.votos().gravar(votos);
                return null;
            }
        };
        tarefa.setOnFailed(e -> {
            registrarErro("gravar votos", tarefa.getException());
            urna.getPainel().exibirMensagemComBotoes(TipoMensagem.ERRO,
                    Mensagens.ERRO_GRAVAR_VOTOS,
                    "TENTAR NOVAMENTE", () -> gravarVotos(urna, votos),
                    "FECHAR", null);
        });
        executar(tarefa);
    }

    private static void executar(Task<?> tarefa) {
        Thread thread = new Thread(tarefa, "urna-servicos");
        thread.setDaemon(true);
        thread.start();
    }

    private static void registrarErro(String acao, Throwable erro) {
        System.err.println("[Urna Cidadã] Falha ao " + acao + ": " + erro);
        if (erro != null) {
            erro.printStackTrace();
        }
    }
}
