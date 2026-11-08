# 🗳️ Urna Cidadã: Simulação Educativa do Voto Eletrônico e Combate à Desinformação

Projeto de extensão acadêmica desenvolvido para a disciplina de **Programação Orientada a Objetos (POO)** na **UniJorge**. O objetivo é simular o funcionamento da urna eletrônica brasileira através de um aplicativo Java (com versão mobile/web responsiva) e promover oficinas educativas sobre desinformação, *fake news* e *deepfakes* para estudantes do Ensino Médio.

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

## 📁 Estrutura do Projeto

O código-fonte está organizado seguindo o padrão de arquitetura em camadas (**MVC - Model-View-Controller**) e modularizado em Java:

```text
urna_projeto_extensao/
├── src/                                  # Diretório raiz dos códigos-fonte
│   ├── module-info.java                  # Declarações do módulo Java e dependências visuais
│   └── br/
│       └── com/
│           ├── urnacidada/               # Pacotes principais da Urna Cidadã
│           │   ├── model/                # Entidades de dados e Enums (Tailane)
│           │   │   ├── Candidato.java
│           │   │   ├── Cargo.java
│           │   │   └── Voto.java
│           │   ├── controller/           # Regras de negócio e lógica da urna (Renan)
│           │   │   └── UrnaController.java
│           │   ├── service/              # Conexão e persistência Firebase (Íris & Renato)
│           │   │   └── FirebaseService.java
│           │   └── view/                 # Interfaces JavaFX / Swing e telas mobile (Íris, Renato & Matheus)
│           │       └── TelaUrna.java
│           └── unijorge/                 # Módulo de inicialização e layout base (UniJorge)
│               └── main/
│                   ├── Main.java         # Classe principal de inicialização (Erickson)
│                   ├── Organizer.java    # Gerenciador de layouts
│                   └── Window.java       # Janela principal do sistema
├── .gitignore                            # Configurações para ignorar arquivos locais/binários
└── README.md                             # Documentação principal do repositório