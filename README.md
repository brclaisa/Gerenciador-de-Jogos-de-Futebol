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

## 🔌 API REST

### Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/jogos` | Criar novo jogo |
| `GET` | `/api/jogos` | Listar todos os jogos |
| `GET` | `/api/jogos/{id}` | Buscar jogo por ID |
| `PUT` | `/api/jogos/{id}/placar` | Atualizar placar |
| `PUT` | `/api/jogos/{id}/status` | Alterar status do jogo |
| `DELETE` | `/api/jogos/{id}` | Remover jogo |

### Exemplos de Uso

#### 1. Criar Novo Jogo
```bash
curl -X POST http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos \
  -H "Content-Type: application/json" \
  -d '{
    "timeA": "Flamengo",
    "timeB": "Palmeiras",
    "dataHoraPartida": "15/01/2024 20:30"
  }'
```

**Resposta de Sucesso:**
```json
{
  "id": 1,
  "timeA": "Flamengo",
  "timeB": "Palmeiras",
  "placarA": 0,
  "placarB": 0,
  "status": "EM_ANDAMENTO",
  "dataHoraPartida": "2024-01-15T20:30:00",
  "dataCriacao": "2024-08-29T00:10:00",
  "dataAtualizacao": "2024-08-29T00:10:00"
}
```

#### 2. Listar Todos os Jogos
```bash
curl -X GET http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos
```

**Resposta:**
```json
[
  {
    "id": 1,
    "timeA": "Flamengo",
    "timeB": "Palmeiras",
    "placarA": 0,
    "placarB": 0,
    "status": "EM_ANDAMENTO",
    "dataHoraPartida": "2024-01-15T20:30:00"
  }
]
```

#### 3. Buscar Jogo por ID
```bash
curl -X GET http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos/1
```

#### 4. Atualizar Placar
```bash
curl -X PUT http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos/1/placar \
  -H "Content-Type: application/json" \
  -d '{
    "placarA": 2,
    "placarB": 1
  }'
```

**Resposta:**
```json
{
  "id": 1,
  "timeA": "Flamengo",
  "timeB": "Palmeiras",
  "placarA": 2,
  "placarB": 1,
  "status": "EM_ANDAMENTO",
  "dataHoraPartida": "2024-01-15T20:30:00"
}
```

#### 5. Encerrar Jogo
```bash
curl -X PUT "http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos/1/status?status=ENCERRADO"
```

**Resposta:**
```json
{
  "id": 1,
  "timeA": "Flamengo",
  "timeB": "Palmeiras",
  "placarA": 2,
  "placarB": 1,
  "status": "ENCERRADO",
  "dataHoraPartida": "2024-01-15T20:30:00"
}
```

#### 6. Remover Jogo
```bash
curl -X DELETE http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos/1
```

### Testando com JavaScript/Fetch

```javascript
// Criar novo jogo
const criarJogo = async () => {
  const response = await fetch('http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      timeA: 'Flamengo',
      timeB: 'Palmeiras',
      dataHoraPartida: '15/01/2024 20:30'
    })
  });
  
  const jogo = await response.json();
  console.log('Jogo criado:', jogo);
};

// Listar jogos
const listarJogos = async () => {
  const response = await fetch('http://localhost:8080/gerenciador-jogos-1.0.0/api/jogos');
  const jogos = await response.json();
  console.log('Jogos:', jogos);
};
```

### Testando com Python/Requests

```python
import requests

# URL base da API
base_url = "http://localhost:8080/gerenciador-jogos-1.0.0/api"

# Criar novo jogo
def criar_jogo():
    dados = {
        "timeA": "Flamengo",
        "timeB": "Palmeiras",
        "dataHoraPartida": "15/01/2024 20:30"
    }
    
    response = requests.post(f"{base_url}/jogos", json=dados)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Erro: {response.status_code}")
        return None

# Listar todos os jogos
def listar_jogos():
    response = requests.get(f"{base_url}/jogos")
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Erro: {response.status_code}")
        return None

# Testar a API
if __name__ == "__main__":
    # Criar jogo
    jogo = criar_jogo()
    if jogo:
        print(f"Jogo criado com ID: {jogo['id']}")
    
    # Listar jogos
    jogos = listar_jogos()
    if jogos:
        print(f"Total de jogos: {len(jogos)}")
```

### Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| `200` | Sucesso - Operação realizada com sucesso |
| `201` | Criado - Recurso criado com sucesso |
| `400` | Bad Request - Dados inválidos |
| `404` | Not Found - Recurso não encontrado |
| `409` | Conflict - Conflito na operação |
| `500` | Internal Server Error - Erro interno do servidor |

### Validações da API

- **Time A e B**: Obrigatórios, mínimo 2 caracteres, máximo 100
- **Data/Hora**: Formato obrigatório: `dd/MM/yyyy HH:mm`
- **Placar**: Números inteiros não negativos
- **Status**: Valores válidos: `EM_ANDAMENTO`, `ENCERRADO`

