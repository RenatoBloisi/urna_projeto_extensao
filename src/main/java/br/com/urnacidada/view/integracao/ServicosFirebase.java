package br.com.urnacidada.view.integracao;

import br.com.urnacidada.service.FirebaseService;

/**
 * Liga a interface JavaFX da urna ao Firebase (Cloud Firestore).
 *
 * <p>A conexão é feita uma única vez, na primeira chamada — que já acontece em thread de
 * fundo (veja {@code UrnaCidadaApp}), então a tela nunca congela. Se a chave não existir ou a
 * internet falhar, a exceção chega à tela, que mostra a mensagem com "TENTAR NOVAMENTE".</p>
 */
public class ServicosFirebase {

    private final FirebaseService firebase = new FirebaseService();
    private boolean conectado;

    private synchronized void garantirConexao() throws FirebaseService.FirebaseServiceException {
        if (!conectado) {
            firebase.conectar();
            conectado = true;
        }
    }

    public ServicosDaUrna comoServicos() {
        return new ServicosDaUrna(
                () -> {                                  // FonteDeCandidatos
                    garantirConexao();
                    return firebase.buscarCandidatos();
                },
                votos -> {                               // RepositorioDeVotos
                    garantirConexao();
                    firebase.gravarVotos(votos);
                });
    }
}
