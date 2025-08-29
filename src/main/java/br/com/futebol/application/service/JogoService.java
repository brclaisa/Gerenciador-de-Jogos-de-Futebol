package br.com.futebol.application.service;

import br.com.futebol.application.dto.AtualizacaoPlacarDTO;
import br.com.futebol.application.dto.JogoDTO;
import br.com.futebol.domain.entity.Jogo;
import br.com.futebol.domain.enums.StatusJogo;
import br.com.futebol.infrastructure.cache.RedisService;
import br.com.futebol.infrastructure.messaging.RabbitMQService;
import br.com.futebol.infrastructure.repository.JogoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para gerenciamento de jogos de futebol.
 * 
 * <p>Esta classe implementa toda a lógica de negócio relacionada aos jogos,
 * incluindo criação, atualização, consulta e remoção. Coordena as operações
 * entre as camadas de domínio, infraestrutura e apresentação.</p>
 * 
 * @author Sistema de Futebol
 * @version 1.0.0
 * @since 2024-01-01
 */
@ApplicationScoped
public final class JogoService {

    /** Logger para esta classe. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JogoService.class);

    /** Repositório de jogos. */
    @Inject
    private JogoRepository jogoRepository;

    /** Serviço de mensageria RabbitMQ. */
    @Inject
    private RabbitMQService rabbitMQService;

    /** Serviço de cache Redis. */
    @Inject
    private RedisService redisService;

    /**
     * Cria um novo jogo.
     * 
     * <p>Valida os dados do jogo, persiste no banco de dados,
     * publica evento de criação e armazena no cache.</p>
     * 
     * @param jogoDTO DTO com os dados do jogo a ser criado
     * @return DTO do jogo criado com ID gerado
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    @Transactional
    public JogoDTO criarJogo(@Valid final JogoDTO jogoDTO) {
        LOGGER.info("Criando novo jogo: {} vs {}", jogoDTO.getTimeA(), 
                jogoDTO.getTimeB());

        Jogo jogo = new Jogo(jogoDTO.getTimeA(), jogoDTO.getTimeB(), 
                jogoDTO.getDataHoraPartida());
        jogo = jogoRepository.salvar(jogo);

        JogoDTO jogoCriado = converterParaDTO(jogo);

        // Publicar evento
        rabbitMQService.publicarJogoCriado(jogoCriado);

        // Armazenar no cache
        redisService.armazenarJogo(jogoCriado);

        LOGGER.info("Jogo criado com sucesso: ID {}", jogo.getId());
        return jogoCriado;
    }

    /**
     * Atualiza o placar de um jogo.
     * 
     * <p>Verifica se o jogo existe e não está encerrado,
     * atualiza o placar e publica evento de atualização.</p>
     * 
     * @param jogoId ID do jogo
     * @param placarDTO DTO com os novos placares
     * @return DTO do jogo atualizado
     * @throws IllegalArgumentException se o jogo não for encontrado
     * @throws IllegalStateException se o jogo estiver encerrado
     */
    @Transactional
    public JogoDTO atualizarPlacar(final Long jogoId, 
            @Valid final AtualizacaoPlacarDTO placarDTO) {
        LOGGER.info("Atualizando placar do jogo {}: {} x {}", jogoId, 
                placarDTO.getPlacarA(), placarDTO.getPlacarB());

        Optional<Jogo> jogoOpt = jogoRepository.buscarPorId(jogoId);
        if (jogoOpt.isEmpty()) {
            throw new IllegalArgumentException("Jogo não encontrado com ID: " + 
                    jogoId);
        }

        Jogo jogo = jogoOpt.get();
        if (jogo.isEncerrado()) {
            throw new IllegalStateException(
                    "Não é possível atualizar placar de jogo encerrado");
        }

        jogo.atualizarPlacar(placarDTO.getPlacarA(), placarDTO.getPlacarB());
        jogo = jogoRepository.atualizar(jogo);

        JogoDTO jogoAtualizado = converterParaDTO(jogo);

        // Publicar evento
        rabbitMQService.publicarPlacarAtualizado(jogoAtualizado);

        // Atualizar cache
        redisService.armazenarPlacar(jogoId, placarDTO.getPlacarA(), 
                placarDTO.getPlacarB());
        redisService.armazenarJogo(jogoAtualizado);

        LOGGER.info("Placar atualizado com sucesso: Jogo {} - {} x {}", 
                jogoId, placarDTO.getPlacarA(), placarDTO.getPlacarB());
        return jogoAtualizado;
    }

    /**
     * Encerra um jogo.
     * 
     * <p>Altera o status do jogo para ENCERRADO e publica
     * evento de encerramento.</p>
     * 
     * @param jogoId ID do jogo a ser encerrado
     * @return DTO do jogo encerrado
     * @throws IllegalArgumentException se o jogo não for encontrado
     * @throws IllegalStateException se o jogo já estiver encerrado
     */
    @Transactional
    public JogoDTO encerrarJogo(final Long jogoId) {
        LOGGER.info("Encerrando jogo: {}", jogoId);

        Optional<Jogo> jogoOpt = jogoRepository.buscarPorId(jogoId);
        if (jogoOpt.isEmpty()) {
            throw new IllegalArgumentException("Jogo não encontrado com ID: " + 
                    jogoId);
        }

        Jogo jogo = jogoOpt.get();
        if (jogo.isEncerrado()) {
            throw new IllegalStateException("Jogo já está encerrado");
        }

        jogo.encerrarJogo();
        jogo = jogoRepository.atualizar(jogo);

        JogoDTO jogoEncerrado = converterParaDTO(jogo);

        // Publicar evento
        rabbitMQService.publicarJogoEncerrado(jogoEncerrado);

        // Atualizar cache
        redisService.armazenarJogo(jogoEncerrado);

        LOGGER.info("Jogo encerrado com sucesso: ID {}", jogoId);
        return jogoEncerrado;
    }

    /**
     * Busca um jogo por ID.
     * 
     * <p>Primeiro tenta buscar do cache Redis, se não encontrar
     * busca do banco de dados e armazena no cache.</p>
     * 
     * @param jogoId ID do jogo
     * @return Optional contendo o DTO do jogo ou vazio se não encontrado
     */
    public Optional<JogoDTO> buscarPorId(final Long jogoId) {
        // Tentar buscar do cache primeiro
        Optional<JogoDTO> jogoCache = redisService.recuperarJogo(jogoId);
        if (jogoCache.isPresent()) {
            LOGGER.debug("Jogo encontrado no cache: {}", jogoId);
            return jogoCache;
        }

        // Se não estiver no cache, buscar do banco
        Optional<Jogo> jogoOpt = jogoRepository.buscarPorId(jogoId);
        if (jogoOpt.isPresent()) {
            JogoDTO jogoDTO = converterParaDTO(jogoOpt.get());
            // Armazenar no cache para próximas consultas
            redisService.armazenarJogo(jogoDTO);
            return Optional.of(jogoDTO);
        }

        return Optional.empty();
    }

    /**
     * Lista todos os jogos.
     * 
     * @return Lista com todos os jogos convertidos para DTOs
     */
    public List<JogoDTO> listarTodos() {
        List<Jogo> jogos = jogoRepository.listarTodos();
        return jogos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista jogos por status específico.
     * 
     * @param status Status dos jogos a serem listados
     * @return Lista de jogos com o status especificado
     */
    public List<JogoDTO> listarPorStatus(final StatusJogo status) {
        List<Jogo> jogos = jogoRepository.listarPorStatus(status);
        return jogos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os jogos em andamento.
     * 
     * @return Lista de jogos em andamento
     */
    public List<JogoDTO> listarEmAndamento() {
        return listarPorStatus(StatusJogo.EM_ANDAMENTO);
    }

    /**
     * Lista todos os jogos encerrados.
     * 
     * @return Lista de jogos encerrados
     */
    public List<JogoDTO> listarEncerrados() {
        return listarPorStatus(StatusJogo.ENCERRADO);
    }

    /**
     * Lista jogos por período de tempo.
     * 
     * @param inicio Data/hora de início do período
     * @param fim Data/hora de fim do período
     * @return Lista de jogos no período especificado
     */
    public List<JogoDTO> listarPorPeriodo(final LocalDateTime inicio, 
            final LocalDateTime fim) {
        List<Jogo> jogos = jogoRepository.listarPorPeriodo(inicio, fim);
        return jogos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Remove um jogo do sistema.
     * 
     * <p>Remove o jogo do banco de dados e do cache Redis.</p>
     * 
     * @param jogoId ID do jogo a ser removido
     * @throws IllegalArgumentException se o jogo não for encontrado
     */
    @Transactional
    public void removerJogo(final Long jogoId) {
        LOGGER.info("Removendo jogo: {}", jogoId);

        if (!jogoRepository.existePorId(jogoId)) {
            throw new IllegalArgumentException("Jogo não encontrado com ID: " + 
                    jogoId);
        }

        jogoRepository.remover(jogoId);

        // Remover do cache
        redisService.removerJogo(jogoId);

        LOGGER.info("Jogo removido com sucesso: ID {}", jogoId);
    }

    /**
     * Obtém estatísticas dos jogos.
     * 
     * @return String formatada com estatísticas dos jogos
     */
    public String obterEstatisticas() {
        long total = jogoRepository.contarTotal();
        long emAndamento = jogoRepository.contarPorStatus(
                StatusJogo.EM_ANDAMENTO);
        long encerrados = jogoRepository.contarPorStatus(StatusJogo.ENCERRADO);

        return String.format("Total: %d | Em Andamento: %d | Encerrados: %d", 
                total, emAndamento, encerrados);
    }

    /**
     * Converte entidade Jogo para DTO.
     * 
     * <p>Método privado para encapsular a lógica de conversão
     * entre entidade e DTO.</p>
     * 
     * @param jogo Entidade a ser convertida
     * @return DTO correspondente à entidade
     */
    private JogoDTO converterParaDTO(final Jogo jogo) {
        JogoDTO dto = new JogoDTO();
        dto.setId(jogo.getId());
        dto.setTimeA(jogo.getTimeA());
        dto.setTimeB(jogo.getTimeB());
        dto.setPlacarA(jogo.getPlacarA());
        dto.setPlacarB(jogo.getPlacarB());
        dto.setStatus(jogo.getStatus());
        dto.setDataHoraPartida(jogo.getDataHoraPartida());
        dto.setDataCriacao(jogo.getDataCriacao());
        dto.setDataAtualizacao(jogo.getDataAtualizacao());
        return dto;
    }
}
