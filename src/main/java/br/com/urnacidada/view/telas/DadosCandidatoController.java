package br.com.urnacidada.view.telas;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.view.apresentacao.Recursos;
import br.com.urnacidada.view.apresentacao.Responsivo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;

/**
 * Controlador da TELA 2 — Dados do candidato ({@code DadosCandidato.fxml}).
 *
 * <p>Nada está fixo no FXML: tudo é preenchido por {@link #setCandidato(Candidato)} ou
 * {@link #setDados(String, String, String, String)}.</p>
 *
 * <p>Usa somente o que EXISTE em {@link Candidato}: {@code getNome()}, {@code getNumero()},
 * {@code getPartido()} e {@code getCargo().getDescricao()}. A classe {@code Candidato}
 * NÃO tem campo de foto; por isso a foto é opcional e vem de {@link #setFoto(Image)} ou
 * da convenção de arquivos (ver {@link Recursos#urlFotoCandidato(String)}).</p>
 *
 * <p>Esta classe apenas EXIBE. Quem busca o candidato é a lógica da urna.</p>
 */
public class DadosCandidatoController {

    private static final double LARGURA_BASE = 760;
    private static final double ALTURA_BASE = 500;
    private static final String TEXTO_AUSENTE = "—";

    @FXML private BorderPane raiz;
    @FXML private Label lblCargo;
    @FXML private HBox boxNumero;
    @FXML private Label lblNome;
    @FXML private Label lblPartido;
    @FXML private StackPane fotoMoldura;
    @FXML private ImageView imgFoto;
    @FXML private VBox boxPlaceholder;
    @FXML private Label lblIniciais;
    @FXML private Label lblLegendaFoto;
    @FXML private Label lblOrientacao;

    @FXML
    private void initialize() {
        Responsivo.escalar(raiz, LARGURA_BASE, ALTURA_BASE);
        // A imagem se ajusta ao tamanho da moldura (que escala junto com a tela).
        imgFoto.fitWidthProperty().bind(fotoMoldura.widthProperty().subtract(4));
        imgFoto.fitHeightProperty().bind(fotoMoldura.heightProperty().subtract(4));
        limpar();
    }

    // ------------------------------------------------------------------
    // API de integração
    // ------------------------------------------------------------------

    /**
     * Exibe os dados de um candidato real. Se existir uma foto em
     * {@code img/candidatos/<numero>.png|jpg}, ela é usada; caso contrário aparece o
     * avatar com as iniciais. Passar {@code null} limpa a tela.
     */
    public void setCandidato(Candidato candidato) {
        if (candidato == null) {
            limpar();
            return;
        }
        aplicarCandidato(candidato);
        URL urlFoto = Recursos.urlFotoCandidato(candidato.getNumero());
        setFoto(urlFoto != null ? new Image(urlFoto.toExternalForm()) : null);
    }

    /** Igual a {@link #setCandidato(Candidato)}, mas com a foto informada (ou {@code null} para avatar). */
    public void setCandidato(Candidato candidato, Image foto) {
        if (candidato == null) {
            limpar();
            return;
        }
        aplicarCandidato(candidato);
        setFoto(foto);
    }

    /**
     * Exibe dados soltos (útil quando não existe um objeto Candidato). NÃO altera a foto atual.
     *
     * @param cargo texto já pronto do cargo (ex.: {@code Cargo.getDescricao()})
     */
    public void setDados(String nome, String numero, String partido, String cargo) {
        lblNome.setText(textoOuAusente(nome));
        lblPartido.setText(textoOuAusente(partido));
        lblCargo.setText(cargo == null ? "" : cargo);
        atualizarCaixasDoNumero(numero);
        lblIniciais.setText(iniciais(nome));
    }

    /** Define a foto; {@code null} (ou imagem com erro) mostra o avatar com as iniciais. */
    public void setFoto(Image foto) {
        boolean temFoto = foto != null && !foto.isError();
        imgFoto.setImage(temFoto ? foto : null);
        imgFoto.setVisible(temFoto);
        boxPlaceholder.setVisible(!temFoto);
    }

    /** Troca o texto de orientação exibido na parte de baixo. */
    public void setOrientacao(String texto) {
        lblOrientacao.setText(texto);
    }

    /** Troca a legenda abaixo da foto. */
    public void setLegendaFoto(String texto) {
        lblLegendaFoto.setText(texto);
    }

    /** Remove todos os dados exibidos (a tela continua na árvore, só fica em branco). */
    public void limpar() {
        lblCargo.setText("");
        lblNome.setText("");
        lblPartido.setText("");
        lblIniciais.setText("");
        boxNumero.getChildren().clear();
        setFoto(null);
    }

    // ------------------------------------------------------------------
    // Internos
    // ------------------------------------------------------------------

    private void aplicarCandidato(Candidato candidato) {
        String cargo = candidato.getCargo() != null ? candidato.getCargo().getDescricao() : null;
        setDados(candidato.getNome(), candidato.getNumero(), candidato.getPartido(), cargo);
    }

    /** Um quadradinho por dígito, como no visor de uma urna. */
    private void atualizarCaixasDoNumero(String numero) {
        boxNumero.getChildren().clear();
        if (numero == null) {
            return;
        }
        for (char digito : numero.toCharArray()) {
            Label caixa = new Label(String.valueOf(digito));
            caixa.getStyleClass().add("digito-caixa");
            boxNumero.getChildren().add(caixa);
        }
    }

    /** Partido pode vir nulo do banco (FirebaseService só exige nome, número e cargo). */
    private static String textoOuAusente(String texto) {
        return (texto == null || texto.isBlank()) ? TEXTO_AUSENTE : texto.trim();
    }

    /** Primeira letra do primeiro e do último nome (ou só a primeira, se houver um nome). */
    private static String iniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "";
        }
        String[] partes = nome.trim().split("\\s+");
        String primeira = partes[0].substring(0, 1);
        if (partes.length == 1) {
            return primeira.toUpperCase();
        }
        String ultima = partes[partes.length - 1].substring(0, 1);
        return (primeira + ultima).toUpperCase();
    }
}
