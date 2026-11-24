package br.com.urnacidada.view.apresentacao;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Carregador de telas FXML. Devolve o nó raiz e o controlador já ligados.
 *
 * <p>Exemplo:</p>
 * <pre>
 *   Telas.Tela&lt;TelaInicialController&gt; t = Telas.carregar(Recursos.FXML_TELA_INICIAL);
 *   t.controlador().setAoIniciar(() -&gt; ...);
 *   painel.getChildren().add(t.raiz());
 * </pre>
 */
public final class Telas {

    /** Par (raiz visual, controlador) resultante do carregamento de um FXML. */
    public record Tela<C>(Parent raiz, C controlador) { }

    private Telas() {
        // classe utilitária
    }

    /**
     * Carrega um FXML a partir de um caminho de {@link Recursos}.
     *
     * @throws IllegalStateException se o arquivo não for encontrado no classpath
     * @throws UncheckedIOException  se o FXML existir mas estiver inválido
     *                               (controlador não encontrado, fx:id errado etc.)
     */
    public static <C> Tela<C> carregar(String caminhoFxml) {
        URL url = Telas.class.getResource(caminhoFxml);
        if (url == null) {
            throw new IllegalStateException("FXML não encontrado no classpath: " + caminhoFxml
                    + " (confira se a pasta recursos/ está dentro de src/ e se o build copiou os arquivos).");
        }
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent raiz = loader.load();
            C controlador = loader.getController();
            return new Tela<>(raiz, controlador);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao carregar o FXML " + caminhoFxml, e);
        }
    }
}
