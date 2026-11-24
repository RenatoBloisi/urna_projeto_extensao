# 🗳️ Urna Cidadã: Simulação Educativa do Voto Eletrônico e Combate à Desinformação

Projeto de extensão acadêmica desenvolvido para a disciplina de **Programação Orientada a Objetos (POO)** no **Centro Universitário Jorge Amado**. O objetivo é simular o funcionamento da urna eletrônica brasileira através de um aplicativo Java (com versão mobile/web responsiva) e promover oficinas educativas sobre desinformação, *fake news* e *deepfakes* para estudantes do Ensino Médio.

---

## 🛠️ Como o Projeto Foi Desenvolvido

O desenvolvimento do simulador eleitoral foi estruturado em seis etapas principais, utilizando **Java**, **JavaFX** e **Firebase Cloud Firestore**, com foco na adaptação para dispositivos móveis:

1. **Java – Modelagem de Classes e Objetos:** Criação das classes `Candidato`, `Voto` e do enum `Cargo` com atributos privados, construtores e métodos *getters/setters*, garantindo o encapsulamento e estruturação dos dados da votação.
2. **Java – Lógica da Votação (`UrnaController`):** Implementação da máquina de estados para busca de candidatos por número e cargo, validação de entradas por limites de dígitos (Deputado Federal: 4, Deputado Estadual: 5, Presidente: 2), correção, confirmação, voto em branco, tratamento de votos nulos e avanço automático entre etapas.
3. **Firebase – Banco de Dados:** Configuração do Cloud Firestore para consulta de candidatos em tempo real, gravação segura dos votos confirmados e tratamento de erros de conexão e persistência.
4. **JavaFX – Interface Visual:** Desenvolvimento de componentes visuais, campos de entrada, teclado numérico, visor e botões de ação, considerando a adaptação layout-responsive para telas móveis.
5. **Interface Web – Acesso Mobile:** Planejamento da versão web responsiva com telas de apresentação de candidatos, etapas da votação e mensagens de confirmação e encerramento adaptadas para *smartphones*.
6. **Integração e Testes:** Conexão da interface gráfica à lógica Java e ao Firebase, testes ponta a ponta dos fluxos de votação, validação dos registros no banco e verificação de compatibilidade mobile.

---

## ▶️ Como Executar

Requisitos: **JDK 17** e **Maven 3.8+** (`java -version` deve mostrar 17).

```bash
mvn javafx:run                 # abre a urna (usa o Firebase)
URNA_MODO=local mvn javafx:run # abre a urna sem Firebase (candidatos fixos, votos só em memória)
mvn compile exec:java          # cadastra os 16 candidatos no Firestore (uma vez)
```

No Windows (PowerShell): `$env:URNA_MODO="local"; mvn javafx:run`.

Configuração do Firebase (chave da conta de serviço, regras, coleções): **[`docs/CONFIGURAR_FIREBASE.md`](docs/CONFIGURAR_FIREBASE.md)**.
Guia da interface JavaFX: [`docs/GUIA_DA_INTERFACE.md`](docs/GUIA_DA_INTERFACE.md).

---

## 📁 Estrutura do Projeto

Projeto **Maven (Java 17 + JavaFX 17 + Firebase Admin SDK)**, organizado em camadas (**MVC**):

```text
urna_projeto_extensao/
├── pom.xml                               # Dependências (JavaFX, Firebase) e Java 17
├── src/main/java/br/com/
│   ├── urnacidada/
│   │   ├── model/                        # Candidato, Cargo, Voto (Tailane)
│   │   ├── controller/                   # UrnaController: lógica da votação (Renan)
│   │   ├── service/                      # FirebaseService e CadastrarCandidatos (Íris & Renato)
│   │   └── view/                         # Interface JavaFX (Íris, Renato & Matheus)
│   │       ├── app/                      # Launcher (execute esta) e UrnaCidadaApp
│   │       ├── integracao/               # Liga a tela ao Firebase (ServicosFirebase) ou ao modo local
│   │       ├── apresentacao/ · telas/    # Componentes e controladores das telas
│   └── unijorge/main/                    # Protótipo inicial em Swing (Erickson)
├── src/main/resources/br/com/urnacidada/view/recursos/   # FXML, CSS e fotos dos candidatos
├── firebase/                             # Regras do Firestore (firestore.rules)
├── config/                               # Chave da conta de serviço (NÃO vai para o Git)
├── docs/                                 # Guias e CSV dos candidatos
├── firebase.json · .firebaserc           # Projeto Firebase (urna-eletronica-2026)
└── .gitignore
```
