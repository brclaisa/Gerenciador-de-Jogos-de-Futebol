# 🚀 Execução Rápida - Gerenciador de Jogos de Futebol

## ⚡ Passos para Executar em 5 Minutos

### 1. Pré-requisitos
```bash
# Verificar se tem Java 21+
java -version

# Verificar se tem Maven
mvn -version

# Verificar se tem Docker
docker --version
docker-compose --version
```

### 2. Compilar o Projeto
```bash
# Na pasta raiz do projeto
mvn clean package
```

### 3. Executar com Docker Compose
```bash
# Iniciar todos os serviços
docker-compose up -d

# Verificar status dos serviços
docker-compose ps

# Ver logs se necessário
docker-compose logs -f wildfly
```

### 4. Acessar a Aplicação
- **Interface Web (Apache Wicket)**: http://localhost:8080/gerenciador-jogos/
- **API REST**: http://localhost:8080/gerenciador-jogos/api/jogos
- **WildFly Admin Console**: http://localhost:9990 (admin/admin)
- **RabbitMQ**: http://localhost:15672 (guest/guest)
- **Redis**: localhost:6379

## 🔧 Comandos Úteis

### Docker
```bash
# Parar serviços
docker-compose down

# Reiniciar
docker-compose restart

# Ver logs específicos
docker-compose logs postgres
docker-compose logs rabbitmq
docker-compose logs redis
```

### Maven
```bash
# Limpar e compilar
mvn clean compile

# Executar testes
mvn test

# Gerar WAR
mvn package
```

## 🐛 Solução de Problemas

### Se o WildFly não iniciar:
```bash
# Verificar logs
docker-compose logs wildfly

# Verificar se as portas estão livres
netstat -tulpn | grep :8080
```

### Se o banco não conectar:
```bash
# Verificar se WildFly está rodando
docker-compose exec wildfly curl -f http://localhost:8080/gerenciador-jogos/

# Verificar logs do WildFly
docker-compose logs wildfly
```

### Se RabbitMQ não funcionar:
```bash
# Verificar status
docker-compose exec rabbitmq rabbitmq-diagnostics status

# Verificar se a porta está livre
netstat -tulpn | grep :5672
```

## 📱 Teste Rápido da API

### Criar um jogo:
```bash
curl -X POST http://localhost:8080/gerenciador-jogos/api/jogos \
  -H "Content-Type: application/json" \
  -d '{
    "timeA": "Flamengo",
    "timeB": "Palmeiras",
    "dataHoraPartida": "2024-01-15T20:00:00"
  }'
```

### Listar jogos:
```bash
curl http://localhost:8080/gerenciador-jogos/api/jogos
```

### Atualizar placar:
```bash
curl -X PUT http://localhost:8080/gerenciador-jogos/api/jogos/1/placar \
  -H "Content-Type: application/json" \
  -d '{
    "placarA": 2,
    "placarB": 1
  }'
```

## 🎯 Próximos Passos

1. **Acesse a interface web** em http://localhost:8080/gerenciador-jogos/
2. **Crie alguns jogos** usando o formulário Wicket
3. **Teste a API REST** com os comandos curl acima
4. **Monitore os logs** para ver os eventos sendo publicados
5. **Verifique o RabbitMQ** para ver as mensagens
6. **Explore o Redis** para ver o cache funcionando

## 🆘 Precisa de Ajuda?

- Verifique os logs: `docker-compose logs`
- Consulte o README.md completo
- Verifique se todas as portas estão livres
- Reinicie os serviços: `docker-compose restart`

---

**🎉 Pronto! Seu sistema está rodando no WildFly com Apache Wicket!**
