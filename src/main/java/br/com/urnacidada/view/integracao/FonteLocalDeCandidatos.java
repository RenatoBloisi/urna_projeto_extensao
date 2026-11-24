package br.com.urnacidada.view.integracao;

import br.com.urnacidada.model.Candidato;
import br.com.urnacidada.model.Cargo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Os 16 candidatos fictícios do projeto, embutidos no código (modo local, sem banco).
 *
 * <p>Convenção do campo {@code partido}: {@code "SIGLA - Nome do partido"}.
 * As fotos ficam em {@code recursos/img/candidatos/<numero>.png}. Os mesmos dados estão em
 * {@code docs/candidatos_ficticios.csv}, para cadastro no Firestore.</p>
 */
public class FonteLocalDeCandidatos implements FonteDeCandidatos {

    @Override
    public List<Candidato> carregar() {
        List<Candidato> lista = new ArrayList<>();

        // Deputado(a) Federal — 4 dígitos
        lista.add(new Candidato("Batman", "2102", "PSG - Partido da Segurança de Gotham", Cargo.DEPUTADO_FEDERAL));
        lista.add(new Candidato("Thor", "2202", "PTR - Partido do Trovão Real", Cargo.DEPUTADO_FEDERAL));
        lista.add(new Candidato("Pantera Negra", "2203", "PWI - Partido de Wakanda e Inovação", Cargo.DEPUTADO_FEDERAL));
        lista.add(new Candidato("Doutor Estranho", "2204", "PAM - Partido das Artes Místicas", Cargo.DEPUTADO_FEDERAL));
        lista.add(new Candidato("Mulher-Maravilha", "2205", "PHE - Partido da Harmonia e Equidade", Cargo.DEPUTADO_FEDERAL));
        lista.add(new Candidato("Laterna Verde", "2206", "PLE - Partido da Luz e Esperança", Cargo.DEPUTADO_FEDERAL));

        // Deputado(a) Estadual — 5 dígitos
        lista.add(new Candidato("Homem-Aranha", "15123", "PTA - Partido de Teia e Ação", Cargo.DEPUTADO_ESTADUAL));
        lista.add(new Candidato("Viúva-Negra", "15224", "PSE - Partido da Estratégia Especial", Cargo.DEPUTADO_ESTADUAL));
        lista.add(new Candidato("Flash", "15335", "PVM - Partido da Velocidade em Movimento", Cargo.DEPUTADO_ESTADUAL));
        lista.add(new Candidato("Wanda Maximoff", "15446", "PCM - Partido do Caos Místico", Cargo.DEPUTADO_ESTADUAL));
        lista.add(new Candidato("Robin", "15557", "PJV - Partido da Justiça Verde", Cargo.DEPUTADO_ESTADUAL));
        lista.add(new Candidato("Capitã Marvel", "15668", "PEL - Partido da Energia e Luz", Cargo.DEPUTADO_ESTADUAL));

        // Presidente(a) — 2 dígitos
        lista.add(new Candidato("Capitão Pátria", "11", "PUC - Partido da União e Coragem", Cargo.PRESIDENTE));
        lista.add(new Candidato("Capitão América", "27", "PDL - Partido da Democracia e Liberdade", Cargo.PRESIDENTE));
        lista.add(new Candidato("Titã Cósmico", "33", "PIT - Partido da Inovação e Tecnologia", Cargo.PRESIDENTE));
        lista.add(new Candidato("Aquaman", "44", "PMO - Partido dos Mares e Oceanos", Cargo.PRESIDENTE));

        return Collections.unmodifiableList(lista);
    }
}
