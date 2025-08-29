# Gerenciador de Jogos de Futebol

Sistema completo de gerenciamento de partidas de futebol com atualizações em tempo real, desenvolvido em Java com arquitetura limpa e moderna.

## 🚀 Características

- **CRUD Completo**: Criação, leitura, atualização e remoção de jogos
- **Atualizações em Tempo Real**: Eventos publicados no RabbitMQ e cache no Redis
- **Interface Web Moderna**: Apache Wicket com design responsivo
- **API REST**: Endpoints completos para integração
- **Arquitetura Limpa**: Separação clara de responsabilidades
- **Docker Compose**: Orquestração completa dos serviços

## 🏗️ Arquitetura

O projeto segue os princípios da **Arquitetura Limpa (Clean Architecture)** com as seguintes camadas:

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

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem principal (LTS mais recente)
- **Jakarta EE 10** - Plataforma empresarial
- **Apache Wicket 10** - Framework web

- **RabbitMQ 5.18** - Message broker
- **Redis 7** - Cache em memória
- **WildFly 37.0.0** - Servidor de aplicação Jakarta EE
- **Maven 3.11** - Gerenciador de dependências
- **Docker Compose** - Orquestração de containers

## 📋 Pré-requisitos

- **Java 21** ou superior (recomendado)
- Maven 3.8+
- Docker e Docker Compose
- Git

## 🚀 Como Executar

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

- **Interface Web**: http://localhost:8080
- **API REST**: http://localhost:8080/api/jogos
- **WildFly Management**: http://localhost:9990
- **RabbitMQ Management**: http://localhost:15672

- **Redis**: localhost:6379

## 📚 Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── br/com/futebol/
│   │       ├── domain/           # Camada de domínio
│   │       │   ├── entity/       # Entidades JPA
│   │       │   └── enums/        # Enums do domínio
│   │       ├── application/      # Camada de aplicação
│   │       │   ├── dto/          # DTOs de transferência
│   │       │   └── service/      # Serviços de aplicação
│   │       ├── infrastructure/   # Camada de infraestrutura
│   │       │   ├── repository/   # Repositórios em memória
│   │       │   ├── rest/         # Controladores REST
│   │       │   ├── messaging/    # Serviços de mensageria
│   │       │   └── cache/        # Serviços de cache
│   │       └── presentation/     # Camada de apresentação
│   │           └── wicket/       # Páginas Wicket
│   ├── resources/                # Recursos da aplicação
│   │   ├── META-INF/            # Configurações da aplicação
│   │   ├── application.properties # Configurações da aplicação
│   │   └── logging.properties   # Configuração de logging
│   └── webapp/                  # Arquivos web estáticos
├── test/                        # Testes unitários (estrutura preparada)
└── wildfly-config/              # Configurações do WildFly
```

## 🔌 API REST

### Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/jogos` | Criar novo jogo |
| `GET` | `/api/jogos` | Listar todos os jogos |
| `GET` | `/api/jogos/{id}` | Buscar jogo por ID |
| `PUT` | `/api/jogos/{id}/placar` | Atualizar placar |
| `PUT` | `/api/jogos/{id}/status` | Alterar status |
| `DELETE` | `/api/jogos/{id}` | Remover jogo |

### Exemplos de Uso

#### Criar Novo Jogo
```bash
curl -X POST http://localhost:8080/api/jogos \
  -H "Content-Type: application/json" \
  -d '{
    "timeA": "Flamengo",
    "timeB": "Palmeiras",
    "dataHoraPartida": "2024-01-15T20:00:00"
  }'
```

#### Atualizar Placar
```bash
curl -X PUT http://localhost:8080/api/jogos/1/placar \
  -H "Content-Type: application/json" \
  -d '{
    "placarA": 2,
    "placarB": 1
  }'
```

#### Encerrar Jogo
```bash
curl -X PUT "http://localhost:8080/api/jogos/1/status?status=ENCERRADO"
```

## 🎯 Funcionalidades

### 1. Cadastro e Gerenciamento de Jogos
- ✅ Criação de novos jogos
- ✅ Atualização de informações
- ✅ Consulta por status
- ✅ Listagem com filtros
- ✅ Remoção de jogos

### 2. Atualizações em Tempo Real
- ✅ Eventos publicados no RabbitMQ
- ✅ Cache de placares no Redis
- ✅ Notificações automáticas

### 3. Interface Web (Apache Wicket)
- ✅ Criação de partidas
- ✅ Alteração de placares
- ✅ Encerramento de jogos
- ✅ Listagem organizada
- ✅ Design responsivo

### 4. Backend Robusto
- ✅ API REST completa
- ✅ Persistência em memória
- ✅ Validações e tratamento de erros
- ✅ Logging estruturado

## 🐳 Docker

### Serviços Disponíveis

- **WildFly**: Servidor de aplicação Jakarta EE
- **RabbitMQ**: Message broker para eventos
- **Redis**: Cache em memória
- **WildFly**: Servidor de aplicação Jakarta EE

### Comandos Úteis

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs de um serviço específico
docker-compose logs -f wildfly

# Parar todos os serviços
docker-compose down

# Reconstruir e reiniciar
docker-compose up -d --build

# Limpar volumes (cuidado!)
docker-compose down -v
```

## 🔧 Configuração

### Variáveis de Ambiente

As configurações podem ser alteradas através de variáveis de ambiente ou arquivos de propriedades:

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
```

### Configurações do WildFly

```xml
<!-- jboss-deployment-structure.xml -->
<module name="org.slf4j" slot="main"/>
<module name="org.apache.commons.logging" slot="main"/>
```

## 🧪 Testes

### Status dos Testes

✅ **Compilação**: Sucesso  
✅ **Empacotamento**: Sucesso  
✅ **Validação Maven**: Sucesso  
⚠️ **Checkstyle**: 830 violações (qualidade de código)  

### Executar Testes

```bash
# Compilar o projeto
mvn clean compile

# Executar testes unitários
mvn test

# Empacotar em WAR
mvn package

# Verificar qualidade do código
mvn checkstyle:check
```

### Cobertura de Código

```bash
mvn jacoco:report
```

## 📊 Monitoramento

### Health Checks

- **Aplicação**: http://localhost:8080/health
- **WildFly**: Verificação automática no Docker
- **RabbitMQ**: Verificação automática no Docker
- **Redis**: Verificação automática no Docker

### Logs

Os logs são configurados para:
- Console (desenvolvimento)
- Arquivo rotativo (produção)
- Níveis configuráveis por pacote

### Métricas

- Contadores de jogos por status
- Estatísticas de uso
- Performance de queries

## 🚀 Deploy

### Desenvolvimento

```bash
# Compilar e executar
mvn clean compile
mvn package
```

### Produção

```bash
# Build do WAR
mvn clean package

# Deploy no WildFly
cp target/gerenciador-jogos-1.0.0.war $WILDFLY_HOME/standalone/deployments/
```

### Docker

```bash
# Build da imagem
docker build -t futebol-app .

# Executar container
docker run -p 8080:8080 futebol-app
```

## 🔍 Qualidade do Código

### Status Atual

O projeto está **funcionalmente correto** e pronto para uso, mas possui algumas violações de qualidade de código:

- **830 violações de Checkstyle** identificadas
- **Problemas principais**:
  - Falta de Javadoc em métodos
  - Espaços em branco no final das linhas
  - Linhas muito longas (>80 caracteres)
  - Problemas de formatação

### Melhorias Recomendadas

1. **Documentação**: Adicionar Javadoc completo
2. **Formatação**: Corrigir espaçamento e quebras de linha
3. **Padrões**: Seguir convenções Java
4. **Testes**: Implementar testes unitários

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 🆘 Suporte

- **Issues**: Use o sistema de issues do GitHub
- **Documentação**: Consulte este README e os comentários no código
- **Comunidade**: Participe das discussões e contribuições

## 🔮 Roadmap

- [ ] **Corrigir violações de Checkstyle**
- [ ] **Implementar testes unitários**
- [ ] **Autenticação e autorização**
- [ ] **API GraphQL**
- [ ] **WebSockets para atualizações em tempo real**
- [ ] **Dashboard administrativo**
- [ ] **Relatórios e estatísticas avançadas**
- [ ] **Integração com APIs externas**
- [ ] **Testes de performance**
- [ ] **CI/CD pipeline**

## 📈 Status do Projeto

| Aspecto | Status | Observações |
|---------|--------|-------------|
| **Compilação** | ✅ Funcionando | Java 21, Maven 3.11 |
| **Empacotamento** | ✅ Funcionando | WAR gerado com sucesso |
| **Funcionalidade** | ✅ Funcionando | Todas as features implementadas |
| **Qualidade** | ⚠️ Melhorias necessárias | 830 violações de Checkstyle |
| **Deploy** | ✅ Pronto | Docker Compose configurado |
| **Documentação** | ✅ Atualizada | README completo |

---

**Desenvolvido com ❤️ em Java 21**

*Última atualização: Agosto 2024*
