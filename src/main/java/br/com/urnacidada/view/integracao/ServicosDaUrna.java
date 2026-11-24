package br.com.urnacidada.view.integracao;

import java.util.Objects;

/**
 * Reúne os dois serviços externos de que a interface precisa: de onde vêm os candidatos e
 * onde os votos são gravados.
 *
 * <p>{@link #locais()} funciona sem internet e sem banco (16 candidatos fixos e votos apenas
 * em memória). Para usar o Firebase, crie um {@code ServicosDaUrna} com as implementações
 * do banco e troque a linha indicada em {@code UrnaCidadaApp.criarServicos()}.</p>
 *
 * @param candidatos fonte dos candidatos
 * @param votos      destino dos votos
 */
public record ServicosDaUrna(FonteDeCandidatos candidatos, RepositorioDeVotos votos) {

    public ServicosDaUrna {
        Objects.requireNonNull(candidatos, "candidatos");
        Objects.requireNonNull(votos, "votos");
    }

    /** Modo local: candidatos fixos e votos guardados só em memória. */
    public static ServicosDaUrna locais() {
        return new ServicosDaUrna(new FonteLocalDeCandidatos(), new RepositorioLocalDeVotos());
    }
}
