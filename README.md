# 🗳️ Urna Cidadã: Simulação Educativa do Voto Eletrônico e Combate à Desinformação

Projeto de extensão acadêmica desenvolvido para a disciplina de **Programação Orientada a Objetos (POO)** na **UniJorge**. O objetivo é simular o funcionamento da urna eletrônica brasileira e promover oficinas educativas sobre desinformação, *fake news* e *deepfakes* para estudantes do Ensino Médio.

---

## 📁 Estrutura do Projeto

O código-fonte está organizado seguindo o padrão de arquitetura em camadas (**MVC - Model-View-Controller**) e preparado para modularização em Java:

```text
urna_projeto_extensao/
├── src/                                  # Diretório raiz dos códigos-fonte
│   ├── module-info.java                  # Declarações do módulo Java e dependências (Swing/AWT)
│   └── br/
│       └── com/
│           ├── urnacidada/               # Pacotes principais da Urna Cidadã
│           │   ├── model/                # Classes de dados (Tailane)
│           │   │   ├── Candidato.java
│           │   │   ├── Cargo.java
│           │   │   └── Voto.java
│           │   ├── controller/           # Regras de negócio e lógica de votação (Renan)
│           │   │   └── UrnaController.java
│           │   ├── service/              # Conexão e integração com o Firebase (Íris & Renato)
│           │   │   └── FirebaseService.java
│           │   └── view/                 # Interface gráfica em Java Swing (Íris, Renato & Teteu)
│           │       └── TelaUrna.java
│           └── unijorge/                 # Módulo de inicialização e layout base (UniJorge)
│               └── main/
│                   ├── Main.java         # Classe principal de inicialização (Eriskinho)
│                   ├── Organizer.java    # Gerenciador de layouts Swing
│                   └── Window.java       # Janela principal do sistema
├── .gitignore                            # Arquivo para ignorar binários e configurações de IDEs
└── README.md                             # Documentação principal do repositório