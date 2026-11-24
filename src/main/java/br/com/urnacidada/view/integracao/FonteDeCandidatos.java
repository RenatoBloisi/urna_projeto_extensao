package br.com.urnacidada.view.integracao;

import br.com.urnacidada.model.Candidato;

import java.util.List;

/**
 * De onde a urna obtém os candidatos.
 *
 * <p>A interface gráfica só conhece este contrato: não sabe se os dados vêm do Firebase,
 * de um arquivo ou de uma lista local. Para ligar o Firebase, implemente esta interface
 * (veja {@code docs/MODELO_INTEGRACAO_FIREBASE.txt}) e a informe em {@link ServicosDaUrna}.</p>
 *
 * <p>O método é chamado em uma <b>thread de fundo</b> (nunca na thread do JavaFX), então
 * pode demorar. Qualquer exceção lançada faz a tela mostrar a mensagem
 * "Não foi possível carregar os dados do candidato." com a opção de tentar novamente.</p>
 */
@FunctionalInterface
public interface FonteDeCandidatos {

    /** Devolve todos os candidatos (de todos os cargos). */
    List<Candidato> carregar() throws Exception;
}
