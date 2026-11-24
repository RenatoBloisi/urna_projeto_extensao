package br.com.urnacidada.view.apresentacao;

/**
 * Tipos visuais de mensagem. Define apenas a APARÊNCIA (cor e ícone);
 * quem decide qual tipo usar é o código que chama (Pessoa 4 / Pessoa 6).
 */
public enum TipoMensagem {

    /** Informação neutra (azul). Ex.: "Aguarde a próxima etapa." */
    INFORMACAO("msg-informacao", "i", "Informação"),
    /** Atenção (amarelo). Ex.: "Confira os dados antes de confirmar." */
    AVISO("msg-aviso", "!", "Atenção"),
    /** Problema (vermelho). Ex.: "Número inválido." */
    ERRO("msg-erro", "\u00D7", "Erro"),
    /** Sucesso (verde). Ex.: "Voto confirmado." */
    SUCESSO("msg-sucesso", "\u2713", "Sucesso"),
    /** Pergunta ao usuário (azul). Ex.: "Deseja corrigir sua escolha?" */
    PERGUNTA("msg-pergunta", "?", "Pergunta");

    private final String classeCss;
    private final String icone;
    private final String tituloPadrao;

    TipoMensagem(String classeCss, String icone, String tituloPadrao) {
        this.classeCss = classeCss;
        this.icone = icone;
        this.tituloPadrao = tituloPadrao;
    }

    public String getClasseCss() {
        return classeCss;
    }

    public String getIcone() {
        return icone;
    }

    public String getTituloPadrao() {
        return tituloPadrao;
    }
}
