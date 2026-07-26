# BFF - Agendador de Tarefas

BFF (Backend For Frontend) responsável por centralizar e orquestrar a comunicação entre os microsserviços do sistema de agendamento de tarefas, expondo uma API única e consistente para o front-end, além de executar o job automático de notificação de tarefas.

## 📌 Sobre o projeto

Este serviço faz parte de uma aplicação distribuída em arquitetura de microsserviços, composta por:

- **BFF** (este repositório) — orquestra as chamadas para os demais serviços e executa o agendamento de notificações
- [**Usuário**](https://github.com/thaisrvieira/usuario) — gerenciamento de usuários, autenticação e endereços (PostgreSQL)
- [**Agendador de Tarefas**](https://github.com/thaisrvieira/agendador-tarefas) — CRUD de tarefas agendadas (MongoDB)
- [**Notificação**](https://github.com/thaisrvieira/notificacao) — envio de e-mails de notificação

O BFF não possui banco de dados próprio: ele consome os demais microsserviços via **Feign Client** e repassa as respostas ao front-end de forma agregada e simplificada.

## 🚀 Tecnologias utilizadas

- **Java 17**
- **Spring Boot 4** / Spring Framework 7
- **Spring Cloud OpenFeign** — comunicação declarativa com os microsserviços, com tratamento de erros customizado
- **Spring Scheduling** (`@EnableScheduling`) — job automático de notificação de tarefas
- **Swagger / OpenAPI** (springdoc-openapi) — documentação interativa da API
- **Maven** — gerenciamento de dependências e build
- **Docker** / Docker Compose — containerização
- **SonarQube** — análise estática de qualidade de código
- **Lombok**

## 🏗️ Arquitetura

```
Front-end → BFF → [Usuário, Agendador de Tarefas, Notificação]
                ↑
         CronService (job agendado)
```

Estrutura em camadas:

```
controller/
  UsuarioController               → Endpoints REST de usuário, endereço, telefone e consulta de CEP
  TarefasController                → Endpoints REST de tarefas
  GlobalExceptionHandler            → Tratamento centralizado de exceções (@ControllerAdvice)

business/
  UsuarioService                    → Orquestração das chamadas ao microsserviço de Usuário
  TarefasService                     → Orquestração das chamadas ao microsserviço de Agendador de Tarefas
  EmailService                        → Orquestração das chamadas ao microsserviço de Notificação
  CronService                          → Job agendado (@Scheduled) de notificação automática de tarefas
  dto/in, dto/out                       → DTOs de requisição e resposta (Usuário, Endereço, Telefone, Tarefas, ViaCEP)
  enums/StatusNotificacaoEnum             → Status da tarefa (PENDENTE, NOTIFICADO, CANCELADO)

infrastructure/
  client/
    UsuarioClient, TarefasClient, EmailClient    → Feign Clients para os 3 microsserviços
    config/
      CorsConfig                                    → Liberação de CORS para o front-end
      FeignConfig / FeignError                        → ErrorDecoder customizado, convertendo códigos HTTP dos
                                                          microsserviços em exceções de negócio específicas
  security/SecurityConfig                            → Esquema de segurança Bearer JWT para o Swagger
  exceptions/                                          → BusinessException, ConflictException,
                                                          ResourceNotFoundException, UnauthorizedException,
                                                          IllegalArgumentException
```

## ⏰ Job automático de notificação (CronService)

Um dos principais diferenciais deste projeto: o BFF executa um **job agendado** (`@Scheduled`, horário configurável via `cron.horario`) que:

1. Autentica-se com um usuário de sistema dedicado (credenciais via propriedades `usuario.email` / `usuario.senha`)
2. Busca no microsserviço de Agendador de Tarefas todas as tarefas com evento previsto para a próxima hora
3. Para cada tarefa encontrada, aciona o microsserviço de Notificação para o disparo do e-mail correspondente
4. Atualiza o status da tarefa para `NOTIFICADO` no microsserviço de Agendador de Tarefas

Esse fluxo integra os 3 microsserviços de forma assíncrona e automatizada, sem intervenção do front-end.

## 🔄 Tratamento de erros entre microsserviços

O BFF possui um **ErrorDecoder customizado** (`FeignError`) que intercepta as respostas de erro HTTP vindas dos microsserviços e as converte em exceções de negócio específicas do próprio BFF (`ConflictException`, `ResourceNotFoundException`, `UnauthorizedException`, `IllegalArgumentException`, `BusinessException`), que por sua vez são tratadas de forma centralizada pelo `GlobalExceptionHandler`, mantendo respostas de erro consistentes para o front-end independentemente de qual microsserviço originou a falha.

## 📋 Funcionalidades

- Cadastro, login, atualização e exclusão de usuários
- Cadastro e atualização de endereços e telefones
- Consulta de endereço a partir do CEP (via ViaCEP, repassado pelo microsserviço de Usuário)
- Criação, busca, atualização e exclusão de tarefas agendadas
- Alteração de status de notificação das tarefas
- Notificação automática por e-mail de tarefas próximas do vencimento, via job agendado
- CORS configurado para consumo por front-end (Angular, porta 4200)
- Documentação completa dos endpoints via Swagger

## ⚙️ Como executar

### Pré-requisitos
- Java 17
- Maven
- Os microsserviços de [Usuário](https://github.com/thaisrvieira/usuario), [Agendador de Tarefas](https://github.com/thaisrvieira/agendador-tarefas) e [Notificação](https://github.com/thaisrvieira/notificacao) em execução (ou via Docker Compose)

### Rodando localmente

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Rodando com Docker Compose

```bash
docker compose up --build
```

## 📖 Documentação da API

Com a aplicação em execução, acesse:

```
http://localhost:8083/swagger-ui/index.html
```

## 🔒 Autenticação

A maioria dos endpoints requer um token JWT (Bearer Token), obtido através do endpoint de login do microsserviço de Usuário. O Swagger já vem configurado com o esquema de autenticação Bearer para facilitar os testes.

## 🧪 Qualidade de código

O projeto é analisado com **SonarQube**, com verificação de bugs, vulnerabilidades e code smells:

```bash
./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=bff-agendador-tarefas -Dsonar.host.url=http://localhost:9000 -Dsonar.login=SEU_TOKEN
```

## 👩‍💻 Autora

**Thaís Rodrigues Vieira**
[LinkedIn](https://www.linkedin.com/in/thais-vieira-8471523a2/) | [GitHub](https://github.com/thaisrvieira)
