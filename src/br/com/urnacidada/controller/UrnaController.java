package br.com.urnacidada.controller;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.model.Cargo;
import br.com.urnacidada.model.Voto;
import br.com.urnacidada.model.Voto.TipoVoto;

import java.util.ArrayList;
import java.util.List;

public class UrnaController {

    private List<Candidato> listaCandidatos;
    private List<Voto> votosRegistrados;

    // Sequência fixa dos 3 cargos
    private Cargo[] ordemCargos;
    private int indiceCargoAtual;

    private StringBuilder numeroDigitado;
    private boolean votoEmBranco;

    public UrnaController(List<Candidato> candidatosCadastrados) {
        this.listaCandidatos = candidatosCadastrados;

        // Define a ordem
        this.ordemCargos = new Cargo[]{
                Cargo.DEPUTADO_FEDERAL,
                Cargo.DEPUTADO_ESTADUAL,
                Cargo.PRESIDENTE
        };

        this.votosRegistrados = new ArrayList<>();
        this.numeroDigitado = new StringBuilder();
        this.indiceCargoAtual = 0;
        this.votoEmBranco = false;
    }

    // retorna o cargo da etapa atual
    public Cargo getCargoAtual(){
        if (isVotacaoEncerrada()){
            return null;
        }
        return ordemCargos[indiceCargoAtual];
    }

    // registra a tecla digitada (Swing)
    public void digitarNumero(char digito){
        if (votoEmBranco || isVotacaoEncerrada()){
            return;
        }
        Cargo cargoAtual = getCargoAtual();
        if (numeroDigitado.length() < cargoAtual.getQtdDigitos()){
            numeroDigitado.append(digito);
        }
    }

    // CORRIGIR
    public void corrigir(){
        numeroDigitado.setLength(0);
        votoEmBranco = false;
    }

    // BRANCO
    public void votarEmBranco(){
        numeroDigitado.setLength(0);
        votoEmBranco = true;
    }

    // Busca candidato cadastrado - numero e cargo
    public Candidato buscarCandidatoAtual(){
        if (votoEmBranco || isVotacaoEncerrada()){
            return null;
        }

        Cargo cargoAtual = getCargoAtual();
        String num = numeroDigitado.toString();

        if (num.length() == cargoAtual.getQtdDigitos()){
            for (Candidato candidato : listaCandidatos) {
                if (candidato.getCargo() == cargoAtual && candidato.getNumero().equals(num)){
                    return candidato;
                }
            }
        }
        return null;
    }

    // CONFIRMAR
    public boolean confirmarVoto(){
        if (isVotacaoEncerrada()){
            return false;
        }

        Cargo cargoAtual = getCargoAtual();
        Voto votoRealizado;

        if (votoEmBranco){
            votoRealizado = new Voto(cargoAtual, "", TipoVoto.BRANCO);
        } else {
            String num = numeroDigitado.toString();

            // impede confirmação se num for menor que QtdDigitos do cargo atual
            if (num.length() < cargoAtual.getQtdDigitos()){
                return false;
            }

            Candidato candidatoEncontrado = buscarCandidatoAtual();
            if (candidatoEncontrado != null){
                votoRealizado = new Voto(cargoAtual, num, TipoVoto.VALIDO);
            } else {
                votoRealizado = new Voto(cargoAtual, num, TipoVoto.NULO);
            }
        }

        votosRegistrados.add(votoRealizado);
        prepararProximoCargo();
        return true;
    }

    private void prepararProximoCargo(){
        numeroDigitado.setLength(0);
        votoEmBranco = false;
        indiceCargoAtual++;
    }

    public boolean isVotacaoEncerrada(){
        return indiceCargoAtual >= ordemCargos.length;
    }

    // GETTERS
    public String getNumeroDigitado() {
        return numeroDigitado.toString();
    }

    public boolean isVotoEmBranco() {
        return votoEmBranco;
    }

    public List<Voto> getVotosRegistrados() {
        return votosRegistrados;
    }
}
