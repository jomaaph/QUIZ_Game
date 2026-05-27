
---

```markdown
# Quiz Game 🎮

Um sistema de Quiz baseado em console desenvolvido em **Java** e estruturado sobre o padrão **DAO (Data Access Object)**, utilizando um banco de dados **MySQL** robusto e otimizado. O projeto conta com autenticação segura de jogadores, gerenciamento dinâmico de perguntas por categorias e computação de pontuação em tempo real via gatilhos de banco de dados (*triggers*).

---

## 🚀 Funcionalidades Principais

* **Autenticação de Usuários**: Cadastro e login de jogadores com criptografia de senhas utilizando hash SHA-256.
* **Gestão de Categorias**: Suporte nativo a categorias estruturadas. O banco já inicia populado com *Tecnologia*, *Finanças* e *Lógica Matemática*.
* **Partidas Dinâmicas**: Registro completo de cada partida iniciada, vinculando o jogador e a categoria escolhida.
* **Auditoria de Respostas**: Mapeamento detalhado de cada resposta fornecida pelo jogador (`partida_pergunta`), permitindo rastrear o histórico exato de acertos e erros.
* **Cálculo Automatizado de Pontos**: Sincronização automática de pontuação no banco de dados utilizando uma *Trigger* pós-inserção de respostas.
* **Otimização de Desempenho**: Índices estrategicamente posicionados nas chaves estrangeiras mais consultadas para garantir alta performance nas leituras e joins.

---

## 🏗️ Arquitetura do Projeto

O código-fonte segue as melhores práticas de separação de conceitos, organizado da seguinte forma:

```text
quiz-java/
├── pom.xml                         # Gerenciamento de dependências (Maven)
└── src/
    └── main/
        ├── java/com/quiz/
        │   ├── Main.java           # Ponto de entrada da aplicação CLI
        │   ├── config/
        │   │   └── DatabaseConnection.java  # Conexão otimizada (Pool HikariCP)
        │   ├── model/              # Classes de Entidade (POJOs)
        │   │   ├── Jogador.java
        │   │   ├── Categoria.java
        │   │   ├── Pergunta.java
        │   │   ├── Partida.java
        │   │   └── PartidaPergunta.java
        │   ├── dao/                # Camada de Persistência (Data Access Object)
        │   │   ├── JogadorDAO.java
        │   │   ├── CategoriaDAO.java
        │   │   ├── PerguntaDAO.java
        │   │   ├── PartidaDAO.java
        │   │   └── PartidaPerguntaDAO.java
        │   ├── service/            # Regras de Negócio e Orquestração
        │   │   ├── JogadorService.java
        │   │   └── QuizService.java
        │   └── util/               # Utilitários (Segurança/Hash)
        │       └── SenhaUtil.java
        └── resources/
            └── config.properties   # Credenciais e propriedades de conexão do banco

```

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java
* **Banco de Dados:** MySQL 8.0+
* **Gerenciador de Dependências:** Maven
* **Pool de Conexões:** HikariCP (para gerenciamento eficiente de conexões JDBC)
* **Segurança:** SHA-256 para hashing de senhas

---

## 🗄️ Modelagem do Banco de Dados

O banco de dados `quiz_game` possui uma estrutura relacional normalizada com restrições de integridade estritas (`ON DELETE RESTRICT` / `ON DELETE CASCADE`):

* **`jogador`**: Armazena as credenciais e timestamp de criação.
* **`categorias`**: Segmentação dos temas do quiz.
* **`perguntas`**: Contém o enunciado, 4 opções de resposta e o mapeamento da alternativa correta via `ENUM('a','b','c','d')`.
* **`partida`**: Cabeçalho da sessão de jogo, guardando a pontuação consolidada.
* **`partida_pergunta`**: Tabela associativa que registra a resposta do usuário para cada pergunta de uma partida específica.

### Automação com Trigger

O banco possui o gatilho `trg_atualiza_ponto` que recalcula o total de acertos automaticamente a cada nova inserção na tabela associativa:

```sql
CREATE TRIGGER trg_atualiza_ponto
AFTER INSERT ON partida_pergunta
FOR EACH ROW
BEGIN
  UPDATE partida
  SET ponto = (SELECT COUNT(*) FROM partida_pergunta WHERE ipartida = NEW.ipartida AND acertou = 1)
  WHERE ipartida = NEW.ipartida;
END;

```

---

## ⚙️ Como Executar o Projeto

### 1. Pré-requisitos

* Java JDK 11 ou superior instalado.
* Maven instalado e configurado no PATH.
* Instância do MySQL ativa.

### 2. Configuração do Banco de Dados

Execute o script SQL contido em `quiz_schema.sql` no seu console ou gerenciador MySQL (ex: MySQL Workbench, DBeaver) para criar o banco de dados, tabelas, triggers, índices e a carga inicial de perguntas:

```bash
mysql -u seu_usuario -p < quiz_schema.sql

```

### 3. Configuração da Aplicação

Abra o arquivo `src/main/resources/config.properties` e atualize com as credenciais do seu ambiente local:

```properties
db.url=jdbc:mysql://localhost:32906/quiz_game?useSSL=false&serverTimezone=UTC
db.user=seu_usuario
db.password=sua_senha

```

### 4. Compilação e Execução

Na raiz do projeto (onde se encontra o arquivo `pom.xml`), execute os comandos:

```bash
# Baixar dependências e compilar o projeto
mvn clean compile

# Executar a aplicação
mvn exec:java -Dexec.mainClass="com.quiz.Main"

```

---

## 📝 Licença

Este projeto foi desenvolvido para fins acadêmicos e de estudo prático de persistência de dados e arquitetura de software. Sinta-se livre para clonar, modificar e expandir!

```

```
