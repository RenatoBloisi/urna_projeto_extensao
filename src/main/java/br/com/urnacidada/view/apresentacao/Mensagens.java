package br.com.urnacidada.view.apresentacao;

/**
 * Textos padronizados das mensagens exibidas ao eleitor.
 * Centralizar aqui evita textos diferentes espalhados pelo código.
 *
 * <p>Esta classe só guarda TEXTOS. As regras que decidem QUANDO cada mensagem
 * aparece pertencem à lógica da urna (Pessoa 2) e à interface principal (Pessoa 4).</p>
 */
public final class Mensagens {

    public static final String NUMERO_INVALIDO = "Número inválido.";
    public static final String CANDIDATO_NAO_ENCONTRADO = "Candidato não encontrado.";
    public static final String CONFIRA_OS_DADOS = "Confira os dados antes de confirmar.";
    public static final String DESEJA_CORRIGIR = "Deseja corrigir sua escolha?";
    public static final String VOTO_CONFIRMADO = "Voto confirmado.";
    public static final String AGUARDE_PROXIMA_ETAPA = "Aguarde a próxima etapa.";
    public static final String ERRO_CARREGAR_CANDIDATO = "Não foi possível carregar os dados do candidato.";

    // Complementares usados pela urna principal (Pessoa 4)
    public static final String NUMERO_INCOMPLETO = "Número incompleto. Digite todos os dígitos.";
    public static final String NUMERO_SEM_CANDIDATO = "Número sem candidato. Se confirmar, o voto será NULO.";
    public static final String VOTO_EM_BRANCO = "VOTO EM BRANCO";
    public static final String CARREGANDO_CANDIDATOS = "Carregando candidatos...";
    public static final String ERRO_GRAVAR_VOTOS = "Não foi possível gravar os votos.";

    private Mensagens() {
        // classe utilitária
    }
}
