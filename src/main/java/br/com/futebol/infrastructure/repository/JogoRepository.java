package br.com.futebol.infrastructure.repository;

import br.com.futebol.domain.entity.Jogo;
import br.com.futebol.domain.enums.StatusJogo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repositório para operações de persistência da entidade Jogo
 * Implementação em memória para demonstração
 */
@ApplicationScoped
@Transactional
public class JogoRepository {

    private final ConcurrentHashMap<Long, Jogo> jogos = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Salva um novo jogo
     */
    public Jogo salvar(Jogo jogo) {
        if (jogo.getId() == null) {
            jogo.setId(idGenerator.getAndIncrement());
        }
        jogos.put(jogo.getId(), jogo);
        return jogo;
    }

    /**
     * Atualiza um jogo existente
     */
    public Jogo atualizar(Jogo jogo) {
        if (jogo.getId() != null && jogos.containsKey(jogo.getId())) {
            jogos.put(jogo.getId(), jogo);
            return jogo;
        }
        throw new IllegalArgumentException("Jogo não encontrado para atualização");
    }

    /**
     * Busca um jogo por ID
     */
    public Optional<Jogo> buscarPorId(Long id) {
        return Optional.ofNullable(jogos.get(id));
    }

    /**
     * Lista todos os jogos
     */
    public List<Jogo> listarTodos() {
        return jogos.values().stream()
            .sorted((j1, j2) -> j2.getDataHoraPartida().compareTo(j1.getDataHoraPartida()))
            .collect(Collectors.toList());
    }

    /**
     * Lista jogos por status
     */
    public List<Jogo> listarPorStatus(StatusJogo status) {
        return jogos.values().stream()
            .filter(jogo -> jogo.getStatus() == status)
            .sorted((j1, j2) -> j2.getDataHoraPartida().compareTo(j1.getDataHoraPartida()))
            .collect(Collectors.toList());
    }

    /**
     * Lista jogos por período
     */
    public List<Jogo> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jogos.values().stream()
            .filter(jogo -> !jogo.getDataHoraPartida().isBefore(inicio) && 
                           !jogo.getDataHoraPartida().isAfter(fim))
            .sorted((j1, j2) -> j2.getDataHoraPartida().compareTo(j1.getDataHoraPartida()))
            .collect(Collectors.toList());
    }

    /**
     * Lista jogos em andamento
     */
    public List<Jogo> listarEmAndamento() {
        return listarPorStatus(StatusJogo.EM_ANDAMENTO);
    }

    /**
     * Lista jogos encerrados
     */
    public List<Jogo> listarEncerrados() {
        return listarPorStatus(StatusJogo.ENCERRADO);
    }

    /**
     * Remove um jogo
     */
    public void remover(Long id) {
        if (jogos.remove(id) == null) {
            throw new IllegalArgumentException("Jogo não encontrado para remoção");
        }
    }

    /**
     * Verifica se um jogo existe por ID
     */
    public boolean existePorId(Long id) {
        return jogos.containsKey(id);
    }

    /**
     * Conta o total de jogos
     */
    public long contarTotal() {
        return jogos.size();
    }

    /**
     * Conta jogos por status
     */
    public long contarPorStatus(StatusJogo status) {
        return jogos.values().stream()
            .filter(jogo -> jogo.getStatus() == status)
            .count();
    }
}
