# 🛒 Sistema de Microsserviços — E-commerce

### Autenticação, Clientes, Produtos, Fornecedores e Vendas com Spring Boot, Spring Cloud, Docker e Kubernetes

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)](https://kubernetes.io/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

Sistema distribuído de **e-commerce**, desenvolvido utilizando uma arquitetura baseada em **microsserviços**, com **Spring Boot**, **Spring Cloud (Eureka, Config Server e Gateway)**, autenticação via **JWT**, comunicação entre serviços com **OpenFeign**, **Docker**, **Docker Compose**, **Kubernetes** e **GitHub Actions**.

O projeto aplica conceitos de microsserviços, service discovery, configuração centralizada, roteamento via API Gateway, autenticação e autorização, comunicação entre serviços, conteinerização, orquestração e integração contínua.

---

## 📌 Objetivos

- Desenvolver uma aplicação utilizando arquitetura de microsserviços.
- Separar as responsabilidades do sistema em serviços independentes.
- Implementar autenticação e geração de tokens JWT.
- Implementar refresh token.
- Centralizar as configurações dos microsserviços com Config Server.
- Registrar e descobrir serviços com Eureka Server.
- Rotear as requisições externas através de um API Gateway.
- Validar o token JWT nas requisições protegidas.
- Implementar comunicação entre microsserviços.
- Utilizar OpenFeign para comunicação entre serviços.
- Implementar a comunicação entre `vendas-service` e `produtos-service`.
- Implementar a comunicação entre `fornecedores-service` e `produtos-service`.
- Criar imagens Docker para os microsserviços.
- Utilizar Docker Compose para executar os serviços em conjunto.
- Utilizar Kubernetes para orquestração dos serviços.
- Automatizar o build do `fornecedores-service` com GitHub Actions.

---

# 🛒 Sobre o Sistema

O sistema é dividido nos seguintes componentes:

- **eureka-server** — servidor de descoberta de serviços.
- **config-server** — servidor de configuração centralizada.
- **gateway** — ponto de entrada da aplicação, responsável pelo roteamento e validação do JWT.
- **auth-service** — cadastro de usuários, login, geração e renovação de tokens JWT.
- **clientes-service** — cadastro e gerenciamento de clientes.
- **produtos-service** — cadastro e gerenciamento de produtos.
- **fornecedores-service** — cadastro e gerenciamento de fornecedores, além da consulta aos produtos.
- **vendas-service** — registro de vendas e consulta aos produtos.

Todos os microsserviços são registrados no **Eureka**, permitindo que sejam localizados pelo nome do serviço.

---

## 🔌 Portas

| Serviço                |  Porta |
| ---------------------- | -----: |
| `eureka-server`        | `8761` |
| `config-server`        | `8888` |
| `produtos-service`     | `8081` |
| `vendas-service`       | `8082` |
| `clientes-service`     | `8083` |
| `fornecedores-service` | `8084` |
| `gateway`              | `8085` |
| `auth-service`         | `8086` |

---

# 🏛️ Arquitetura

```text
                              ┌─────────────────────┐
                              │       CLIENTE       │
                              │      REST / HTTP    │
                              └──────────┬──────────┘
                                         │
                                         ▼
                              ┌─────────────────────┐
                              │       GATEWAY       │
                              │   JWT + Discovery   │
                              │      :8085          │
                              └──────────┬──────────┘
                                         │
             ┌───────────────┬───────────┼───────────┬───────────────┐
             ▼               ▼           ▼           ▼               ▼
      ┌─────────────┐ ┌────────────┐ ┌──────────┐ ┌──────────────┐
      │    AUTH     │ │  CLIENTES  │ │ PRODUTOS │ │  FORNECEDORES │
      │   :8086     │ │   :8083    │ │  :8081   │ │    :8084     │
      └─────────────┘ └────────────┘ └────┬─────┘ └──────┬───────┘
                                          │              │
                                          │   OpenFeign  │
                                          │◄─────────────┘
                                          │
                                          ▼
                                  ┌──────────────┐
                                  │    VENDAS    │
                                  │    :8082     │
                                  └──────────────┘

                    ┌──────────────────────────────┐
                    │       EUREKA SERVER          │
                    │            :8761             │
                    └──────────────────────────────┘

                    ┌──────────────────────────────┐
                    │        CONFIG SERVER         │
                    │            :8888             │
                    └──────────────────────────────┘
```

O **Gateway** utiliza o Eureka para descobrir os microsserviços registrados. As comunicações internas entre serviços também utilizam o mecanismo de descoberta.

---

# 🧩 Componentes da Aplicação

| Componente               | Responsabilidade                                     |
| ------------------------ | ---------------------------------------------------- |
| **eureka-server**        | Registro e descoberta de serviços                    |
| **config-server**        | Configuração centralizada                            |
| **gateway**              | Roteamento e validação do JWT                        |
| **auth-service**         | Cadastro, login e autenticação                       |
| **clientes-service**     | Gerenciamento de clientes                            |
| **produtos-service**     | Gerenciamento de produtos                            |
| **fornecedores-service** | Gerenciamento de fornecedores e consulta de produtos |
| **vendas-service**       | Registro de vendas e consulta de produtos            |

---

# 🔐 Autenticação

A autenticação utiliza **JWT (JSON Web Token)**.

O responsável pela autenticação é o:

```text
auth-service
```

### Fluxo de autenticação

```text
Usuário
   │
   │ login
   ▼
auth-service
   │
   │ valida credenciais
   ▼
JWT
   │
   │ Authorization: Bearer <token>
   ▼
Gateway
   │
   │ valida JWT
   ▼
Microsserviço protegido
```

As requisições sem uma credencial válida são rejeitadas pelo Gateway com:

```text
401 Unauthorized
```

---

# 🔑 auth-service

O `auth-service` é responsável pela autenticação dos usuários.

### Responsabilidades

- Cadastrar usuários.
- Autenticar usuários.
- Validar credenciais.
- Criptografar senhas utilizando BCrypt.
- Gerar tokens JWT.
- Renovar tokens através de refresh.
- Persistir os usuários em seu próprio banco de dados.

### Endpoints públicos

| Método | Endpoint            | Descrição                 |
| ------ | ------------------- | ------------------------- |
| POST   | `/usuarios`         | Cadastro de usuário       |
| POST   | `/usuarios/login`   | Login e obtenção do token |
| POST   | `/usuarios/refresh` | Renovação do token        |

---

# 🔓 Endpoints Públicos

As seguintes rotas não exigem autenticação:

```text
POST /usuarios
POST /usuarios/login
POST /usuarios/refresh
```

Essas rotas são liberadas pelo Gateway para permitir o cadastro, login e renovação da credencial.

---

# 🔒 Endpoints Protegidos

Os demais endpoints exigem um JWT válido.

| Método | Endpoint                 | Descrição                               |
| ------ | ------------------------ | --------------------------------------- |
| GET    | `/clientes`              | Lista clientes                          |
| GET    | `/produtos`              | Lista produtos                          |
| GET    | `/produtos/{id}`         | Busca produto por ID                    |
| GET    | `/fornecedores`          | Lista fornecedores                      |
| GET    | `/fornecedores/produtos` | Consulta produtos através do fornecedor |
| GET    | `/vendas`                | Consulta vendas                         |
| POST   | `/vendas`                | Registra uma venda                      |

O token deve ser enviado no cabeçalho:

```http
Authorization: Bearer <token>
```

---

# 🔄 Refresh Token

O sistema disponibiliza:

```http
POST /usuarios/refresh
```

O endpoint permite renovar a credencial de acesso utilizando o mecanismo de refresh implementado pelo `auth-service`.

Fluxo:

```text
Access Token
     │
     │ expira / precisa ser renovado
     ▼
POST /usuarios/refresh
     │
     ▼
Novo token de acesso
```

---

# 🚦 Proteção das Rotas

O Gateway possui um `TokenFilter` responsável por verificar as requisições.

O filtro:

1. Identifica a rota acessada.
2. Verifica se a rota é pública.
3. Procura o cabeçalho `Authorization`.
4. Verifica o formato `Bearer`.
5. Valida o JWT.
6. Libera a requisição quando o token é válido.
7. Retorna `401 Unauthorized` quando o token é ausente ou inválido.

```text
Requisição
    │
    ▼
Gateway
    │
    ├── Rota pública ──────► encaminha
    │
    └── Rota protegida
            │
            ▼
       Possui JWT?
        │       │
       não     sim
        │       │
       401      ▼
             JWT válido?
              │      │
             não    sim
              │      │
             401    ▼
                 encaminha
```

---

# 👥 clientes-service

O `clientes-service` é responsável pelo gerenciamento dos clientes.

### Responsabilidades

- Listar clientes.
- Persistir os dados em seu próprio banco.
- Popular dados iniciais através do `DataInitializer`.

### Endpoint

| Método | Endpoint    | Descrição               |
| ------ | ----------- | ----------------------- |
| GET    | `/clientes` | Lista todos os clientes |

---

# 📦 produtos-service

O `produtos-service` é responsável pelo gerenciamento dos produtos.

### Responsabilidades

- Listar produtos.
- Buscar produto por ID.
- Persistir os dados em seu próprio banco.
- Popular dados iniciais.
- Responder às consultas feitas por outros microsserviços.

### Endpoints

| Método | Endpoint         | Descrição               |
| ------ | ---------------- | ----------------------- |
| GET    | `/produtos`      | Lista todos os produtos |
| GET    | `/produtos/{id}` | Busca produto por ID    |

---

# 🏭 fornecedores-service

O `fornecedores-service` é responsável pelo gerenciamento dos fornecedores.

### Responsabilidades

- Listar fornecedores.
- Persistir os fornecedores.
- Disponibilizar informações de fornecedores.
- Consultar produtos através do `produtos-service`.
- Utilizar **OpenFeign** para comunicação entre microsserviços.
- Registrar-se no Eureka.

### Endpoints

| Método | Endpoint                 | Descrição                                    |
| ------ | ------------------------ | -------------------------------------------- |
| GET    | `/fornecedores`          | Lista todos os fornecedores                  |
| GET    | `/fornecedores/produtos` | Lista produtos através do `produtos-service` |

### Comunicação com produtos-service

O `fornecedores-service` utiliza um cliente Feign:

```text
FORNECEDORES-SERVICE
        │
        │ OpenFeign
        │ GET /produtos
        ▼
PRODUTOS-SERVICE
        │
        ▼
 Lista de produtos
```

O serviço é localizado através do nome registrado no Eureka, evitando a necessidade de configurar diretamente o endereço do `produtos-service`.

---

# 💰 vendas-service

O `vendas-service` é responsável pelo registro das vendas.

### Responsabilidades

- Registrar vendas.
- Consultar produtos.
- Validar o produto informado.
- Obter os dados necessários do produto.
- Persistir as vendas em seu próprio banco.

### Endpoints

| Método | Endpoint  | Descrição               |
| ------ | --------- | ----------------------- |
| GET    | `/vendas` | Endpoint de consulta    |
| POST   | `/vendas` | Registra uma nova venda |

---

# 🔄 Comunicação entre Microsserviços

O projeto utiliza comunicação entre microsserviços através do **Service Discovery** e **OpenFeign**.

### Vendas → Produtos

```text
VENDAS-SERVICE
      │
      │ GET /produtos/{id}
      ▼
PRODUTOS-SERVICE
      │
      ▼
   Produto
```

### Fornecedores → Produtos

```text
FORNECEDORES-SERVICE
        │
        │ OpenFeign
        │ GET /produtos
        ▼
PRODUTOS-SERVICE
        │
        ▼
   Produtos
```

O Eureka permite localizar os serviços pelo nome registrado, evitando dependência de endereços IP fixos.

---

# 🧭 eureka-server

O `eureka-server` é o servidor de **Service Discovery** da aplicação.

Os microsserviços se registram no Eureka ao iniciar, permitindo que outros componentes localizem os serviços pelo nome.

Entre os serviços registrados estão:

```text
AUTH-SERVICE
CLIENTES-SERVICE
FORNECEDORES-SERVICE
GATEWAY
PRODUTOS-SERVICE
VENDAS-SERVICE
```

O Eureka está disponível na porta:

```text
8761
```

---

# ⚙️ config-server

O `config-server` centraliza as configurações dos microsserviços.

As configurações são armazenadas no diretório:

```text
config-repo/
```

Atualmente estão presentes configurações para os serviços:

```text
config-repo/
├── auth-service.properties
├── auth-service-docker.properties
├── clientes-service.properties
├── clientes-service-docker.properties
├── fornecedores-service.properties
├── fornecedores-service-docker.properties
├── produtos-service.properties
├── produtos-service-docker.properties
├── vendas-service.properties
└── vendas-service-docker.properties
```

As configurações com sufixo `-docker` são utilizadas no ambiente de containers, onde os serviços são acessados através dos nomes definidos no Docker Compose.

---

# 🚪 gateway

O `gateway` é o ponto único de entrada da aplicação.

Ele possui duas responsabilidades principais:

- Descobrir e rotear requisições para os microsserviços através do Eureka.
- Validar o token JWT através do `TokenFilter`.

O Gateway utiliza o **Discovery Locator**, permitindo localizar os serviços registrados no Eureka.

Exemplo de acesso ao `fornecedores-service`:

```text
GET http://localhost:8085/fornecedores-service/fornecedores
```

Com autenticação:

```http
Authorization: Bearer <token>
```

Resposta esperada:

```text
HTTP/1.1 200 OK
```

---

# 🐳 Docker

Os microsserviços possuem seus próprios `Dockerfile` para criação das imagens.

Os containers utilizam Java 17 no ambiente Docker.

Exemplo de estrutura:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
```

A porta exposta deve corresponder à porta utilizada pelo respectivo microsserviço.

---

# 🐳 Docker Compose

O projeto possui um `docker-compose.yml` responsável por executar os componentes em containers.

Entre os serviços estão:

```text
eureka-server
config-server
gateway
auth-service
clientes-service
produtos-service
fornecedores-service
vendas-service
```

### Executar

```bash
docker compose up --build
```

Ou em segundo plano:

```bash
docker compose up --build -d
```

### Verificar os containers

```bash
docker compose ps
```

O `fornecedores-service` utiliza a porta:

```text
8084:8084
```

---

# ☸️ Kubernetes

Após a validação com Docker e Docker Compose, a aplicação pode ser executada em Kubernetes através dos manifestos presentes em:

```text
k8s/
```

Estrutura:

```text
k8s/
├── 00-namespace.yaml
├── 01-eureka-server.yaml
├── 02-config-server.yaml
├── 03-produtos-service.yaml
├── 04-vendas-service.yaml
├── 05-gateway.yaml
├── 06-clientes-service.yaml
├── 07-auth-service.yaml
└── README.md
```

### Aplicar os manifestos

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-eureka-server.yaml
kubectl apply -f k8s/02-config-server.yaml
kubectl apply -f k8s/03-produtos-service.yaml
kubectl apply -f k8s/04-vendas-service.yaml
kubectl apply -f k8s/05-gateway.yaml
kubectl apply -f k8s/06-clientes-service.yaml
kubectl apply -f k8s/07-auth-service.yaml
```

### Verificar os recursos

```bash
kubectl get pods
kubectl get deployments
kubectl get services
```

---

# 🤖 GitHub Actions

O projeto possui um workflow de **Continuous Integration** em:

```text
.github/
└── workflows/
    └── ci.yml
```

O workflow é executado automaticamente a cada `push`.

### Etapas do pipeline

```text
Push
 │
 ▼
GitHub Actions
 │
 ├── Checkout do repositório
 │
 ├── Configuração do Java 17
 │
 └── Build do fornecedores-service com Maven
```

O build é executado através de:

```bash
mvn -f fornecedores-service/pom.xml clean package -DskipTests
```

O resultado do pipeline pode ser acompanhado pela aba **Actions** do repositório.

---

# 📁 Estrutura do Projeto

```text
.
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── auth-service/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── clientes-service/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── config-repo/
│   ├── auth-service.properties
│   ├── auth-service-docker.properties
│   ├── clientes-service.properties
│   ├── clientes-service-docker.properties
│   ├── fornecedores-service.properties
│   ├── fornecedores-service-docker.properties
│   ├── produtos-service.properties
│   ├── produtos-service-docker.properties
│   ├── vendas-service.properties
│   └── vendas-service-docker.properties
│
├── config-server/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── eureka-server/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── fornecedores-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── ...
│   │       └── resources/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── gateway/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── k8s/
│   ├── 00-namespace.yaml
│   ├── 01-eureka-server.yaml
│   ├── 02-config-server.yaml
│   ├── 03-produtos-service.yaml
│   ├── 04-vendas-service.yaml
│   ├── 05-gateway.yaml
│   ├── 06-clientes-service.yaml
│   ├── 07-auth-service.yaml
│   └── README.md
│
├── produtos-service/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── vendas-service/
│   ├── src/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── pom.xml
│
├── .gitignore
└── docker-compose.yml
```

---

# 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3**
- **Spring Cloud Netflix Eureka**
- **Spring Cloud Config**
- **Spring Cloud Gateway**
- **Spring Cloud OpenFeign**
- **Spring Data JPA**
- **Spring Security / JWT**
- **PostgreSQL**
- **Maven**
- **Docker**
- **Docker Compose**
- **Kubernetes**
- **GitHub Actions**

---

# 🚀 Como Executar

## 1. Gerar os projetos

Em cada microsserviço:

```bash
mvn clean package
```

---

## 2. Criar as imagens Docker

```bash
docker build -t eureka-server ./eureka-server
docker build -t config-server ./config-server
docker build -t gateway ./gateway
docker build -t auth-service ./auth-service
docker build -t clientes-service ./clientes-service
docker build -t produtos-service ./produtos-service
docker build -t fornecedores-service ./fornecedores-service
docker build -t vendas-service ./vendas-service
```

---

## 3. Executar com Docker Compose

Na raiz do projeto:

```bash
docker compose up --build -d
```

Verifique os serviços:

```bash
docker compose ps
```

---

## 4. Testar o Gateway

Após obter um JWT através do `auth-service`, as rotas protegidas podem ser acessadas utilizando:

```http
Authorization: Bearer <token>
```

Exemplo:

```bash
curl.exe -i "http://localhost:8085/fornecedores-service/fornecedores" -H "Authorization: Bearer <token>"
```

Resposta esperada:

```text
HTTP/1.1 200 OK
```

---

## 5. Executar no Kubernetes

Com o cluster ativo:

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-eureka-server.yaml
kubectl apply -f k8s/02-config-server.yaml
kubectl apply -f k8s/03-produtos-service.yaml
kubectl apply -f k8s/04-vendas-service.yaml
kubectl apply -f k8s/05-gateway.yaml
kubectl apply -f k8s/06-clientes-service.yaml
kubectl apply -f k8s/07-auth-service.yaml
```

Verifique:

```bash
kubectl get pods
kubectl get deployments
kubectl get services
```

---

# 📚 Conceitos Aplicados

- Arquitetura de microsserviços.
- Service Discovery com Eureka.
- Configuração centralizada com Config Server.
- API Gateway.
- Roteamento baseado em descoberta de serviços.
- Autenticação e autorização com JWT.
- Refresh Token.
- APIs REST.
- Comunicação síncrona entre microsserviços.
- OpenFeign.
- Dockerfile.
- Docker Image.
- Docker Container.
- Docker Compose.
- Kubernetes.
- Pods, Deployments e Services.
- Continuous Integration com GitHub Actions.
- Separação de responsabilidades entre serviços.

---

# 👩‍💻 Autora

**Letícia Gomes**

Projeto desenvolvido para a disciplina de **Microsserviços e DevOps com Spring Boot e Spring Cloud**, do bloco de **Desenvolvimento de Softwares Escaláveis**, aplicando conceitos de arquitetura de microsserviços, Spring Cloud, autenticação JWT, comunicação com OpenFeign, Docker, Docker Compose, Kubernetes e GitHub Actions.
