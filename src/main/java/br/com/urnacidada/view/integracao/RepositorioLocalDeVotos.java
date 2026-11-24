package br.com.urnacidada.view.integracao;

import br.com.urnacidada.model.Voto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Guarda os votos apenas em memória (some ao fechar o programa) e os mostra no console.
 * Usado quando o banco de dados ainda não está ligado.
 */
public class RepositorioLocalDeVotos implements RepositorioDeVotos {

    private final List<Voto> gravados = new ArrayList<>();

    @Override
    public synchronized void gravar(List<Voto> votos) {
        gravados.addAll(votos);
        System.out.println("[Urna Cidadã] Modo local: votos guardados apenas em memória:");
        for (Voto voto : votos) {
            System.out.println("  " + voto);
        }
    }

    /** Todos os votos recebidos desde que o programa abriu. */
    public synchronized List<Voto> getGravados() {
        return Collections.unmodifiableList(new ArrayList<>(gravados));
    }
}
