package br.com.urnacidada.view.apresentacao;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.view.apresentacao.Telas.Tela;
import br.com.urnacidada.view.telas.DadosCandidatoController;
import br.com.urnacidada.view.telas.MensagemController;
import br.com.urnacidada.view.telas.TelaFinalController;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * PONTO ÚNICO DE INTEGRAÇÃO das telas da Pessoa 5 (Dados do candidato, Mensagens e Final).
 *
 * <p>Um {@link StackPane} com três camadas (de baixo para cima): dados do candidato,
 * tela final e mensagens. A Pessoa 4 só precisa colocar {@link #getRaiz()} dentro do
 * visor da urna e chamar os métodos {@code exibir...}.</p>
 *
 * <pre>
 *   PainelApresentacao painel = new PainelApresentacao();
 *   visor.getChildren().add(painel.getRaiz());      // visor = área da tela da urna (Pessoa 4)
 *
 *   painel.exibirCandidato(candidato);              // candidato vindo de UrnaController
 *   painel.exibirMensagem(TipoMensagem.ERRO, Mensagens.NUMERO_INVALIDO);
 *   painel.exibirFim(null);                         // só quando a lógica disser que acabou
 * </pre>
 *
 * <p>Esta classe NÃO contém regra de votação, NÃO acessa o Firebase e NÃO decide
 * quando cada tela aparece: apenas mostra o que lhe mandam.</p>
 *
 * <p>Deve ser criada na thread do JavaFX (Application Thread).</p>
 */
public class PainelApresentacao {

    private final StackPane raiz = new StackPane();

    private final Tela<DadosCandidatoController> telaDados;
    private final Tela<TelaFinalController> telaFinal;
    private final Tela<MensagemController> telaMensagem;

    /**
     * Carrega as três telas.
     *
     * @throws IllegalStateException se algum FXML não for encontrado
     * @throws java.io.UncheckedIOException se algum FXML estiver inválido
     */
    public PainelApresentacao() {
        telaDados = Telas.carregar(Recursos.FXML_DADOS_CANDIDATO);
        telaFinal = Telas.carregar(Recursos.FXML_TELA_FINAL);
        telaMensagem = Telas.carregar(Recursos.FXML_MENSAGEM);

        // Ordem = camadas: a última fica por cima.
        raiz.getChildren().addAll(telaDados.raiz(), telaFinal.raiz(), telaMensagem.raiz());

        mostrar(telaDados.raiz(), false);
        mostrar(telaFinal.raiz(), false);
        // A camada de mensagem já nasce oculta (MensagemController.initialize).
    }

    /** Nó a ser inserido no visor da urna. */
    public StackPane getRaiz() {
        return raiz;
    }

    // ------------------------------------------------------------------
    // Candidato
    // ------------------------------------------------------------------

    /** Mostra os dados de um candidato (foto procurada em img/candidatos/&lt;numero&gt;.png). */
    public void exibirCandidato(Candidato candidato) {
        if (candidato == null) {
            ocultarCandidato();
            return;
        }
        telaDados.controlador().setCandidato(candidato);
        mostrar(telaFinal.raiz(), false);
        mostrar(telaDados.raiz(), true);
    }

    /** Mostra os dados de um candidato com uma foto específica (ou {@code null} = avatar). */
    public void exibirCandidato(Candidato candidato, Image foto) {
        if (candidato == null) {
            ocultarCandidato();
            return;
        }
        telaDados.controlador().setCandidato(candidato, foto);
        mostrar(telaFinal.raiz(), false);
        mostrar(telaDados.raiz(), true);
    }

    /** Esconde a ficha do candidato (ex.: ao corrigir ou ao trocar de cargo). */
    public void ocultarCandidato() {
        telaDados.controlador().limpar();
        mostrar(telaDados.raiz(), false);
    }

    // ------------------------------------------------------------------
    // Mensagens
    // ------------------------------------------------------------------

    /** Mensagem sem botões; fica até {@link #ocultarMensagem()}. */
    public void exibirMensagem(TipoMensagem tipo, String texto) {
        telaMensagem.controlador().exibir(tipo, texto);
    }

    /** Mensagem que some sozinha. */
    public void exibirMensagemTemporaria(TipoMensagem tipo, String texto, Duration duracao) {
        telaMensagem.controlador().exibirTemporaria(tipo, texto, duracao);
    }

    /** Mensagem com um botão de fechar/continuar. */
    public void exibirMensagemComBotao(TipoMensagem tipo, String texto, String rotuloBotao, Runnable aoClicar) {
        telaMensagem.controlador().exibirComBotao(tipo, texto, rotuloBotao, aoClicar);
    }

    /** Pergunta com dois botões; as ações recebidas são executadas após o clique. */
    public void exibirConfirmacao(String texto, String rotuloSim, Runnable aoSim,
                                  String rotuloNao, Runnable aoNao) {
        telaMensagem.controlador().exibirConfirmacao(texto, rotuloSim, aoSim, rotuloNao, aoNao);
    }

    /** Mensagem de qualquer tipo com dois botões (ex.: erro com "TENTAR NOVAMENTE" / "VOLTAR"). */
    public void exibirMensagemComBotoes(TipoMensagem tipo, String texto,
                                        String rotuloPrimario, Runnable aoPrimario,
                                        String rotuloSecundario, Runnable aoSecundario) {
        telaMensagem.controlador().exibirComBotoes(tipo, texto, rotuloPrimario, aoPrimario,
                rotuloSecundario, aoSecundario);
    }

    public void ocultarMensagem() {
        telaMensagem.controlador().ocultar();
    }

    public boolean isMensagemExibida() {
        return telaMensagem.controlador().isExibindo();
    }

    // ------------------------------------------------------------------
    // Tela final
    // ------------------------------------------------------------------

    /**
     * Mostra a tela "FIM". Deve ser chamada SOMENTE quando a lógica da urna informar que
     * todas as etapas terminaram (ex.: {@code UrnaController.isVotacaoEncerrada() == true}).
     *
     * @param aoReiniciar ação do botão "VOLTAR AO INÍCIO"; {@code null} = botão oculto
     */
    public void exibirFim(Runnable aoReiniciar) {
        telaMensagem.controlador().ocultar();
        mostrar(telaDados.raiz(), false);
        telaFinal.controlador().setAoReiniciar(aoReiniciar);
        mostrar(telaFinal.raiz(), true);
    }

    /** Volta ao estado inicial: tudo oculto (nenhuma ficha, mensagem ou tela final). */
    public void reiniciar() {
        telaMensagem.controlador().ocultar();
        telaDados.controlador().limpar();
        mostrar(telaDados.raiz(), false);
        mostrar(telaFinal.raiz(), false);
    }

    // ------------------------------------------------------------------
    // Acesso avançado (opcional)
    // ------------------------------------------------------------------

    public DadosCandidatoController getControladorDados() {
        return telaDados.controlador();
    }

    public MensagemController getControladorMensagem() {
        return telaMensagem.controlador();
    }

    public TelaFinalController getControladorFinal() {
        return telaFinal.controlador();
    }

    private static void mostrar(Node no, boolean visivel) {
        no.setVisible(visivel);
        no.setManaged(visivel);
    }
}
