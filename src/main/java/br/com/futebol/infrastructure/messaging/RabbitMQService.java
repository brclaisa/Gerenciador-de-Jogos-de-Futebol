package br.com.futebol.infrastructure.messaging;

import br.com.futebol.application.dto.JogoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Serviço para integração com RabbitMQ
 */
@ApplicationScoped
public class RabbitMQService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    private static final String EXCHANGE_NAME = "jogos.futebol";
    private static final String QUEUE_PLACAR_ATUALIZADO = "placar.atualizado";
    private static final String QUEUE_JOGO_CRIADO = "jogo.criado";
    private static final String QUEUE_JOGO_ENCERRADO = "jogo.encerrado";

    private Connection connection;
    private Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void inicializar() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(System.getProperty("rabbitmq.host", "localhost"));
            factory.setPort(Integer.parseInt(System.getProperty("rabbitmq.port", "5672")));
            factory.setUsername(System.getProperty("rabbitmq.username", "guest"));
            factory.setPassword(System.getProperty("rabbitmq.password", "guest"));

            connection = factory.newConnection();
            channel = connection.createChannel();

            // Declarar exchange
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

            // Declarar filas
            channel.queueDeclare(QUEUE_PLACAR_ATUALIZADO, true, false, false, null);
            channel.queueDeclare(QUEUE_JOGO_CRIADO, true, false, false, null);
            channel.queueDeclare(QUEUE_JOGO_ENCERRADO, true, false, false, null);

            // Binding das filas com o exchange
            channel.queueBind(QUEUE_PLACAR_ATUALIZADO, EXCHANGE_NAME, "jogo.placar.atualizado");
            channel.queueBind(QUEUE_JOGO_CRIADO, EXCHANGE_NAME, "jogo.criado");
            channel.queueBind(QUEUE_JOGO_ENCERRADO, EXCHANGE_NAME, "jogo.encerrado");

            logger.info("RabbitMQ inicializado com sucesso");

        } catch (Exception e) {
            logger.error("Erro ao inicializar RabbitMQ", e);
        }
    }

    /**
     * Publica evento de placar atualizado
     */
    public void publicarPlacarAtualizado(JogoDTO jogo) {
        publicarEvento("jogo.placar.atualizado", jogo);
    }

    /**
     * Publica evento de jogo criado
     */
    public void publicarJogoCriado(JogoDTO jogo) {
        publicarEvento("jogo.criado", jogo);
    }

    /**
     * Publica evento de jogo encerrado
     */
    public void publicarJogoEncerrado(JogoDTO jogo) {
        publicarEvento("jogo.encerrado", jogo);
    }

    /**
     * Publica um evento genérico
     */
    private void publicarEvento(String routingKey, Object payload) {
        try {
            if (channel != null && channel.isOpen()) {
                String mensagem = objectMapper.writeValueAsString(payload);
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, mensagem.getBytes());
                logger.info("Evento publicado: {} - {}", routingKey, mensagem);
            } else {
                logger.warn("Canal RabbitMQ não está disponível");
            }
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar payload para JSON", e);
        } catch (IOException e) {
            logger.error("Erro ao publicar evento no RabbitMQ", e);
        }
    }

    @PreDestroy
    public void destruir() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            logger.info("Conexões RabbitMQ fechadas");
        } catch (IOException | TimeoutException e) {
            logger.error("Erro ao fechar conexões RabbitMQ", e);
        }
    }

    /**
     * Verifica se o serviço está disponível
     */
    public boolean isDisponivel() {
        return channel != null && channel.isOpen();
    }
}
