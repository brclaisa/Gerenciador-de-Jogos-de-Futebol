package br.com.futebol.infrastructure.cache;

import br.com.futebol.application.dto.JogoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Optional;

/**
 * Serviço para integração com Redis.
 * 
 * <p>Gerencia o cache de dados de jogos e placares utilizando Redis
 * como sistema de armazenamento em memória.</p>
 * 
 * @author Sistema de Futebol
 * @version 1.0.0
 * @since 2024-01-01
 */
@ApplicationScoped
public final class RedisService {

    /** Logger para esta classe. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    /** Prefixo para chaves de placar no Redis. */
    private static final String PREFIXO_PLACAR = "jogo:placar:";

    /** Prefixo para chaves de dados completos no Redis. */
    private static final String PREFIXO_JOGO = "jogo:dados:";

    /** Tempo de expiração padrão em segundos (1 hora). */
    private static final int TEMPO_EXPIRACAO = 3600;

    /** Configurações do pool de conexões. */
    private static final int MAX_TOTAL_CONNECTIONS = 20;
    private static final int MAX_IDLE_CONNECTIONS = 10;
    private static final int MIN_IDLE_CONNECTIONS = 5;
    private static final int CONNECTION_TIMEOUT_MS = 2000;
    private static final int EVICTION_RUN_INTERVAL_MINUTES = 5;
    private static final int MIN_EVICTABLE_IDLE_TIME_MINUTES = 1;

    /** Pool de conexões Redis. */
    private JedisPool jedisPool;

    /** Mapper para conversão JSON. */
    private final ObjectMapper objectMapper;

    /**
     * Construtor padrão.
     * 
     * <p>Inicializa o ObjectMapper com suporte a tipos de data Java 8+.</p>
     */
    public RedisService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Inicializa o serviço Redis.
     * 
     * <p>Configura o pool de conexões e testa a conectividade
     * com o servidor Redis.</p>
     */
    @PostConstruct
    public void inicializar() {
        try {
            String host = System.getProperty("redis.host", "localhost");
            int port = Integer.parseInt(System.getProperty("redis.port", "6379"));
            String password = System.getProperty("redis.password", null);

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(MAX_TOTAL_CONNECTIONS);
            poolConfig.setMaxIdle(MAX_IDLE_CONNECTIONS);
            poolConfig.setMinIdle(MIN_IDLE_CONNECTIONS);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinEvictableIdleTime(
                    Duration.ofMinutes(MIN_EVICTABLE_IDLE_TIME_MINUTES));
            poolConfig.setTimeBetweenEvictionRuns(
                    Duration.ofMinutes(EVICTION_RUN_INTERVAL_MINUTES));

            if (password != null && !password.isEmpty()) {
                jedisPool = new JedisPool(poolConfig, host, port, 
                        CONNECTION_TIMEOUT_MS, password);
            } else {
                jedisPool = new JedisPool(poolConfig, host, port, 
                        CONNECTION_TIMEOUT_MS);
            }

            // Testar conexão
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.ping();
                LOGGER.info("Redis inicializado com sucesso em {}:{}", host, port);
            }

        } catch (Exception e) {
            LOGGER.error("Erro ao inicializar Redis", e);
        }
    }

    /**
     * Armazena o placar de um jogo no cache.
     * 
     * @param jogoId ID do jogo
     * @param placarA Placar do time A
     * @param placarB Placar do time B
     */
    public void armazenarPlacar(final Long jogoId, final Integer placarA, 
            final Integer placarB) {
        try (Jedis jedis = jedisPool.getResource()) {
            String chave = PREFIXO_PLACAR + jogoId;
            String valor = placarA + ":" + placarB;
            jedis.setex(chave, TEMPO_EXPIRACAO, valor);
            LOGGER.debug("Placar armazenado no Redis: {} = {}", chave, valor);
        } catch (Exception e) {
            LOGGER.error("Erro ao armazenar placar no Redis", e);
        }
    }

    /**
     * Recupera o placar de um jogo do cache.
     * 
     * @param jogoId ID do jogo
     * @return Optional contendo o placar no formato "A:B" ou vazio se não encontrado
     */
    public Optional<String> recuperarPlacar(final Long jogoId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String chave = PREFIXO_PLACAR + jogoId;
            String valor = jedis.get(chave);
            return Optional.ofNullable(valor);
        } catch (Exception e) {
            LOGGER.error("Erro ao recuperar placar do Redis", e);
            return Optional.empty();
        }
    }

    /**
     * Armazena dados completos de um jogo no cache.
     * 
     * @param jogo DTO do jogo a ser armazenado
     */
    public void armazenarJogo(final JogoDTO jogo) {
        try (Jedis jedis = jedisPool.getResource()) {
            String chave = PREFIXO_JOGO + jogo.getId();
            String valor = objectMapper.writeValueAsString(jogo);
            jedis.setex(chave, TEMPO_EXPIRACAO, valor);
            LOGGER.debug("Jogo armazenado no Redis: {} = {}", chave, valor);
        } catch (JsonProcessingException e) {
            LOGGER.error("Erro ao serializar jogo para JSON", e);
        } catch (Exception e) {
            LOGGER.error("Erro ao armazenar jogo no Redis", e);
        }
    }

    /**
     * Recupera dados de um jogo do cache.
     * 
     * @param jogoId ID do jogo
     * @return Optional contendo o DTO do jogo ou vazio se não encontrado
     */
    public Optional<JogoDTO> recuperarJogo(final Long jogoId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String chave = PREFIXO_JOGO + jogoId;
            String valor = jedis.get(chave);
            if (valor != null) {
                JogoDTO jogo = objectMapper.readValue(valor, JogoDTO.class);
                return Optional.of(jogo);
            }
            return Optional.empty();
        } catch (JsonProcessingException e) {
            LOGGER.error("Erro ao deserializar jogo do JSON", e);
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Erro ao recuperar jogo do Redis", e);
            return Optional.empty();
        }
    }

    /**
     * Remove um jogo do cache.
     * 
     * @param jogoId ID do jogo a ser removido
     */
    public void removerJogo(final Long jogoId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(PREFIXO_PLACAR + jogoId);
            jedis.del(PREFIXO_JOGO + jogoId);
            LOGGER.debug("Jogo removido do Redis: {}", jogoId);
        } catch (Exception e) {
            LOGGER.error("Erro ao remover jogo do Redis", e);
        }
    }

    /**
     * Limpa todo o cache.
     * 
     * <p><strong>Atenção:</strong> Esta operação remove todos os dados
     * armazenados no Redis.</p>
     */
    public void limparCache() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushAll();
            LOGGER.info("Cache Redis limpo com sucesso");
        } catch (Exception e) {
            LOGGER.error("Erro ao limpar cache Redis", e);
        }
    }

    /**
     * Verifica se o serviço está disponível.
     * 
     * @return true se o Redis estiver acessível, false caso contrário
     */
    public boolean isDisponivel() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtém estatísticas do cache.
     * 
     * @return String com informações de estatísticas do Redis
     */
    public String obterEstatisticas() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.info();
        } catch (Exception e) {
            LOGGER.error("Erro ao obter estatísticas do Redis", e);
            return "Erro ao obter estatísticas";
        }
    }

    /**
     * Destrói o serviço e libera recursos.
     * 
     * <p>Fecha o pool de conexões Redis.</p>
     */
    @PreDestroy
    public void destruir() {
        if (jedisPool != null) {
            jedisPool.close();
            LOGGER.info("Pool Redis fechado");
        }
    }
}
