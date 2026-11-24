package br.com.urnacidada.service;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.view.integracao.FonteLocalDeCandidatos;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Ferramenta de linha de comando: cadastra os 16 candidatos fictícios na coleção
 * {@code candidatos} do Firestore. Pode ser executada quantas vezes quiser — o documento de
 * cada candidato usa o número eleitoral como ID, então rodar de novo apenas sobrescreve.
 *
 * <pre>mvn compile exec:java</pre>
 */
public final class CadastrarCandidatos {

    private CadastrarCandidatos() {
    }

    public static void main(String[] args) {
        try {
            FirebaseService servico = new FirebaseService();
            servico.conectar();
            Firestore db = FirestoreClient.getFirestore();

            List<Candidato> candidatos = new FonteLocalDeCandidatos().carregar();
            WriteBatch lote = db.batch();
            for (Candidato c : candidatos) {
                Map<String, Object> dados = new HashMap<>();
                dados.put("nome", c.getNome());
                dados.put("numero", c.getNumero());
                dados.put("partido", c.getPartido());
                dados.put("cargo", c.getCargo().name());
                lote.set(db.collection("candidatos").document(c.getNumero()), dados);
            }
            lote.commit().get(30, TimeUnit.SECONDS);

            System.out.println("OK: " + candidatos.size() + " candidatos gravados na coleção 'candidatos'.");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("ERRO ao cadastrar candidatos: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
