package br.com.urnacidada.view.integracao;

import br.com.urnacidada.model.Voto;

import java.util.List;

/**
 * Onde os votos são gravados ao final da votação.
 *
 * <p>Para ligar o Firebase, implemente esta interface (veja
 * {@code docs/MODELO_INTEGRACAO_FIREBASE.txt}) e a informe em {@link ServicosDaUrna}.</p>
 *
 * <p>O método é chamado em uma <b>thread de fundo</b> (nunca na thread do JavaFX). Se lançar
 * exceção, a tela mostra "Não foi possível gravar os votos." com a opção de tentar novamente.</p>
 */
@FunctionalInterface
public interface RepositorioDeVotos {

    /** Grava os votos de uma sessão de votação (um por cargo). */
    void gravar(List<Voto> votos) throws Exception;
}
