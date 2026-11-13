package br.com.urnacidada.service;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.model.Cargo;
import br.com.urnacidada.model.Voto;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FirebaseService {

    private static final String CAMINHO_CHAVE = "config/serviceAccountKey.json";
    private static final int TIMEOUT_SEGUNDOS = 10;

    private Firestore db;

    // Exceção própria para a interface tratar erros de forma simples
    public static class FirebaseServiceException extends Exception {
        public FirebaseServiceException(String mensagem, Throwable causa) {
            super(mensagem, causa);
        }
    }

    // CONEXÃO
    public void conectar() throws FirebaseServiceException {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                try (FileInputStream chave = new FileInputStream(CAMINHO_CHAVE)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(chave))
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            }
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            throw new FirebaseServiceException(
                "Não foi possível ler a chave do Firebase em " + CAMINHO_CHAVE, e);
        } catch (Exception e) {
            throw new FirebaseServiceException("Falha ao conectar ao Firebase.", e);
        }
    }

    // CONSULTA DE CANDIDATOS
    public List<Candidato> buscarCandidatos() throws FirebaseServiceException {
        verificarConexao();
        List<Candidato> lista = new ArrayList<>();
        try {
            QuerySnapshot resultado = db.collection("candidatos")
                    .get()
                    .get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);

            for (QueryDocumentSnapshot doc : resultado.getDocuments()) {
                String nome = doc.getString("nome");
                String numero = doc.getString("numero");
                String partido = doc.getString("partido");
                String cargoTxt = doc.getString("cargo");

                if (nome == null || numero == null || cargoTxt == null) {
                    continue; // ignora documento incompleto
                }
                try {
                    Cargo cargo = Cargo.valueOf(cargoTxt);
                    lista.add(new Candidato(nome, numero, partido, cargo));
                } catch (IllegalArgumentException e) {
                    System.err.println("Cargo inválido no documento " + doc.getId() + ": " + cargoTxt);
                }
            }
            return lista;
        } catch (TimeoutException e) {
            throw new FirebaseServiceException("Tempo esgotado ao buscar candidatos. Verifique a internet.", e);
        } catch (ExecutionException e) {
            throw new FirebaseServiceException("Erro ao consultar candidatos no banco.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirebaseServiceException("Consulta interrompida.", e);
        }
    }

    // GRAVAÇÃO DE VOTOS
    public void gravarVotos(List<Voto> votos) throws FirebaseServiceException {
        verificarConexao();
        try {
            WriteBatch lote = db.batch(); // grava tudo ou nada
            for (Voto voto : votos) {
                Map<String, Object> dados = new HashMap<>();
                dados.put("cargo", voto.getCargo().name());
                dados.put("numero", voto.getNumero());
                dados.put("tipo", voto.getTipoVoto().name());
                dados.put("dataHora", com.google.cloud.Timestamp.now());
                lote.set(db.collection("votos").document(), dados);
            }
            lote.commit().get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new FirebaseServiceException("Tempo esgotado ao gravar os votos.", e);
        } catch (ExecutionException e) {
            throw new FirebaseServiceException("Erro ao gravar os votos no banco.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FirebaseServiceException("Gravação interrompida.", e);
        }
    }

    private void verificarConexao() throws FirebaseServiceException {
        if (db == null) {
            throw new FirebaseServiceException("Firebase não conectado. Chame conectar() primeiro.", null);
        }
    }
}