# Gerenciador de Jogos de Futebol

Sistema de gerenciamento de partidas de futebol, desenvolvido em Java 21.

## 🏗️ Arquitetura

O projeto segue os princípios **Clean Architecture** com as seguintes camadas:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Apache Wicket │  │   REST API      │  │   Web UI    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   JogoService   │  │   DTOs          │  │   Validators│ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │     Jogo        │  │   StatusJogo    │  │   Business  │ │
│  │   Entity        │  │     Enum        │  │   Rules     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Repository    │  │   RabbitMQ      │  │    Redis    │ │
│  │   (In-Memory)   │  │   Service       │  │   Service   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tecnologias

- **Java 21** - Linguagem principal
- **Jakarta EE 10** - Plataforma empresarial
- **Apache Wicket 10** - Framework web
- **RabbitMQ 5.18** - Message broker
- **Redis 7** - Cache em memória
- **WildFly 37.0.0** - Servidor de aplicação
- **Maven 3.11** - Gerenciador de dependências
- **Docker Compose** - Orquestração de containers

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.8+
- Docker e Docker Compose
- Git

## 🚀 Instalação

### 1. Clone o Repositório
```bash
git clone <url-do-repositorio>
cd projeto2
```

### 2. Compile o Projeto
```bash
mvn clean package
```

### 3. Execute com Docker Compose
```bash
docker-compose up -d
```

### 4. Acesse a Aplicação
- **Interface Web**: http://localhost:8080/gerenciador-jogos-1.0.0/
- **API REST**: http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos
- **WildFly Management**: http://localhost:9990
- **RabbitMQ Management**: http://localhost:15672

## 📚 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── br/com/futebol/
│   │       ├── domain/           # Entidades e regras de negócio
│   │       ├── application/      # Serviços e DTOs
│   │       ├── infrastructure/   # Repositórios, REST e mensageria
│   │       └── presentation/     # Interface Wicket
│   ├── resources/                # Configurações
│   └── webapp/                  # Arquivos web estáticos
└── test/                        # Testes unitários
```

## 🐳 Comandos Docker Úteis

```bash
# Iniciar serviços
docker-compose up -d

# Ver logs
docker-compose logs -f wildfly

# Parar serviços
docker-compose down

# Verificar status
docker ps
```

## ⚙️ Configurações

### Variáveis de Ambiente

As configurações podem ser alteradas através de variáveis de ambiente:

```properties
# Aplicação
app.name=Gerenciador de Jogos de Futebol
app.version=1.0.0

# RabbitMQ
rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.username=guest
rabbitmq.password=guest

# Redis
redis.host=localhost
redis.port=6379
redis.password=

# WildFly
wildfly.port=8080
wildfly.management.port=9990
```

### Configurações do Docker Compose

```yaml
# docker-compose.yml
version: '3.8'
services:
  wildfly:
    ports:
      - "8080:8080"      # Porta da aplicação
      - "9990:9990"      # Porta de gerenciamento
  
  rabbitmq:
    ports:
      - "5672:5672"      # Porta AMQP
      - "15672:15672"    # Porta de gerenciamento
  
  redis:
    ports:
      - "6379:6379"      # Porta Redis
```

---

