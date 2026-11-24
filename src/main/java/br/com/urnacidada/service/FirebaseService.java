package br.com.urnacidada.service;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.model.Cargo;
import br.com.urnacidada.model.Voto;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FirebaseService {

    /** Pasta (ao lado do pom.xml) onde fica a chave da conta de serviço. Ela NÃO vai para o Git. */
    private static final String PASTA_CONFIG = "config";
    private static final String CHAVE_PADRAO = PASTA_CONFIG + "/serviceAccountKey.json";
    private static final int TIMEOUT_SEGUNDOS = 10;

    private Firestore db;

    // Exceção própria para a interface tratar erros de forma simples
    public static class FirebaseServiceException extends Exception {
        public FirebaseServiceException(String mensagem, Throwable causa) {
            super(mensagem, causa);
        }
    }

    // CONEXÃO
    /**
     * Conecta ao Firebase (uma única vez por programa). A chave da conta de serviço é procurada, nesta ordem:
     * <ol>
     *   <li>propriedade {@code -Durna.firebase.chave=caminho/da/chave.json}</li>
     *   <li>variável de ambiente {@code URNA_FIREBASE_CHAVE} ou {@code GOOGLE_APPLICATION_CREDENTIALS}</li>
     *   <li>{@code config/serviceAccountKey.json}</li>
     *   <li>qualquer {@code config/*serviceAccountKey*.json} (nome original baixado do Firebase)</li>
     * </ol>
     */
    public synchronized void conectar() throws FirebaseServiceException {
        Path chave = localizarChave();
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                try (InputStream entrada = Files.newInputStream(chave)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(entrada))
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            }
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            throw new FirebaseServiceException(
                "Não foi possível ler a chave do Firebase em " + chave.toAbsolutePath(), e);
        } catch (Exception e) {
            throw new FirebaseServiceException("Falha ao conectar ao Firebase.", e);
        }
    }

    /** Devolve o caminho da chave da conta de serviço ou lança erro explicando onde colocá-la. */
    static Path localizarChave() throws FirebaseServiceException {
        List<String> candidatos = new ArrayList<>();
        candidatos.add(System.getProperty("urna.firebase.chave"));
        candidatos.add(System.getenv("URNA_FIREBASE_CHAVE"));
        candidatos.add(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        candidatos.add(CHAVE_PADRAO);

        for (String caminho : candidatos) {
            if (caminho != null && !caminho.isBlank() && Files.isRegularFile(Paths.get(caminho))) {
                return Paths.get(caminho);
            }
        }

        // Nome original baixado do Firebase (ex.: urna-eletronica-2026-firebase-adminsdk-xxxx.json)
        try (var arquivos = Files.newDirectoryStream(Paths.get(PASTA_CONFIG), "*serviceAccountKey*.json")) {
            for (Path arquivo : arquivos) {
                return arquivo;
            }
        } catch (IOException e) {
            // pasta config/ inexistente: cai na mensagem abaixo
        }

        throw new FirebaseServiceException(
            "Chave do Firebase não encontrada. Salve o arquivo da conta de serviço como "
            + Paths.get(CHAVE_PADRAO).toAbsolutePath()
            + " (veja docs/CONFIGURAR_FIREBASE.md).", null);
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