package br.com.urnacidada.view.app;

import javafx.application.Application;

/**
 * Ponto de entrada do programa (EXECUTE ESTA CLASSE).
 *
 * <p>Fica separada da {@link UrnaCidadaApp} porque, em alguns ambientes, executar diretamente
 * uma classe que estende {@code Application} gera o erro
 * "JavaFX runtime components are missing".</p>
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Application.launch(UrnaCidadaApp.class, args);
    }
}
