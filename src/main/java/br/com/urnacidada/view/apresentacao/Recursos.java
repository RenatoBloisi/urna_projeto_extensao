package br.com.urnacidada.view.apresentacao;

import java.net.URL;

/**
 * Caminhos centralizados dos recursos visuais (FXML, CSS e imagens).
 *
 * <p>Todos os caminhos são ABSOLUTOS dentro do classpath (começam com "/") e
 * correspondem às pastas em {@code src/main/resources/br/com/urnacidada/view/recursos/}.
 * Se algum arquivo for movido de pasta, só esta classe precisa mudar.</p>
 */
public final class Recursos {

    private static final String BASE = "/br/com/urnacidada/view/recursos/";

    public static final String FXML_TELA_INICIAL    = BASE + "fxml/TelaInicial.fxml";
    public static final String FXML_DADOS_CANDIDATO = BASE + "fxml/DadosCandidato.fxml";
    public static final String FXML_MENSAGEM        = BASE + "fxml/Mensagem.fxml";
    public static final String FXML_TELA_FINAL      = BASE + "fxml/TelaFinal.fxml";
    public static final String FXML_TELA_URNA       = BASE + "fxml/TelaUrna.fxml";

    /** Referência informativa: o CSS é carregado pelos próprios FXML (via {@code <stylesheets>}). */
    public static final String CSS_PRINCIPAL = BASE + "css/urna-cidada.css";

    /** Pasta das fotos: coloque aqui arquivos nomeados com o número do candidato (ex.: 1234.png). */
    private static final String PASTA_FOTOS = BASE + "img/candidatos/";
    private static final String[] EXTENSOES_FOTO = {".png", ".jpg", ".jpeg"};

    private Recursos() {
        // classe utilitária
    }

    /**
     * Procura a foto de um candidato pelo número eleitoral.
     * Convenção: {@code img/candidatos/<numero>.png} (ou .jpg / .jpeg).
     *
     * @return a URL da imagem, ou {@code null} se não existir foto para esse número
     *         (nesse caso a tela mostra o avatar com as iniciais).
     */
    public static URL urlFotoCandidato(String numero) {
        // Só aceita dígitos, para nunca montar um caminho estranho a partir de dados externos.
        if (numero == null || !numero.matches("\\d+")) {
            return null;
        }
        for (String extensao : EXTENSOES_FOTO) {
            URL url = Recursos.class.getResource(PASTA_FOTOS + numero + extensao);
            if (url != null) {
                return url;
            }
        }
        return null;
    }
}
