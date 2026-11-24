package br.com.urnacidada.view.apresentacao;

import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Region;

import java.util.Locale;

/**
 * Faz uma tela "escalar" junto com a janela.
 *
 * <p>O CSS usa unidades "em" (1em = tamanho da fonte da tela). Esta classe observa o
 * tamanho do nó raiz e ajusta o {@code -fx-font-size} dele; todo o layout cresce ou
 * diminui proporcionalmente, mantendo o equilíbrio visual sem sobreposição.</p>
 *
 * <p>Requisito: o nó raiz deve ocupar toda a área disponível (ex.: ser filho de um
 * StackPane ou ser a raiz da Scene).</p>
 */
public final class Responsivo {

    private static final double FONTE_BASE_PX = 16.0;
    private static final double FONTE_MIN_PX = 9.0;
    private static final double FONTE_MAX_PX = 30.0;

    private Responsivo() {
        // classe utilitária
    }

    /**
     * @param raiz         nó raiz da tela
     * @param larguraBase  largura (px) em que o layout foi desenhado para ter fonte de 16px
     * @param alturaBase   altura (px) idem
     */
    public static void escalar(Region raiz, double larguraBase, double alturaBase) {
        ChangeListener<Number> ouvinte = (obs, antigo, novo) -> aplicar(raiz, larguraBase, alturaBase);
        raiz.widthProperty().addListener(ouvinte);
        raiz.heightProperty().addListener(ouvinte);
        aplicar(raiz, larguraBase, alturaBase);
    }

    private static void aplicar(Region raiz, double larguraBase, double alturaBase) {
        double largura = raiz.getWidth();
        double altura = raiz.getHeight();
        if (largura <= 0 || altura <= 0) {
            return; // ainda sem layout
        }
        double escala = Math.min(largura / larguraBase, altura / alturaBase);
        double px = Math.max(FONTE_MIN_PX, Math.min(FONTE_MAX_PX, FONTE_BASE_PX * escala));
        px = Math.round(px * 2) / 2.0; // passos de 0,5px evitam recalcular o CSS a cada pixel

        // Locale.ROOT é obrigatório: em pt-BR o formato padrão usaria vírgula ("14,5px"),
        // o que invalidaria o CSS.
        String estilo = String.format(Locale.ROOT, "-fx-font-size: %.1fpx;", px);
        if (!estilo.equals(raiz.getStyle())) {
            raiz.setStyle(estilo);
        }
    }
}
