package br.com.futebol.infrastructure.repository;

import br.com.futebol.domain.entity.Jogo;
import br.com.futebol.domain.enums.StatusJogo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade Jogo
 * Implementação JPA para persistência no banco de dados
 */
@ApplicationScoped
@Transactional
public class JogoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Salva um novo jogo
     */
    public Jogo salvar(Jogo jogo) {
        try {
            if (jogo.getId() == null) {
                entityManager.persist(jogo);
            } else {
                jogo = entityManager.merge(jogo);
            }
            return jogo;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar jogo: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza um jogo existente
     */
    public Jogo atualizar(Jogo jogo) {
        try {
            return entityManager.merge(jogo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar jogo: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um jogo por ID
     */
    public Optional<Jogo> buscarPorId(Long id) {
        try {
            Jogo jogo = entityManager.find(Jogo.class, id);
            return Optional.ofNullable(jogo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar jogo por ID: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos os jogos
     */
    public List<Jogo> listarTodos() {
        try {
            TypedQuery<Jogo> query = entityManager.createQuery(
                "SELECT j FROM Jogo j ORDER BY j.dataHoraPartida DESC", Jogo.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar todos os jogos: " + e.getMessage(), e);
        }
    }

    /**
     * Lista jogos por status
     */
    public List<Jogo> listarPorStatus(StatusJogo status) {
        try {
            TypedQuery<Jogo> query = entityManager.createQuery(
                "SELECT j FROM Jogo j WHERE j.status = :status ORDER BY j.dataHoraPartida DESC", Jogo.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar jogos por status: " + e.getMessage(), e);
        }
    }

    /**
     * Lista jogos por período
     */
    public List<Jogo> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        try {
            TypedQuery<Jogo> query = entityManager.createQuery(
                "SELECT j FROM Jogo j WHERE j.dataHoraPartida BETWEEN :inicio AND :fim ORDER BY j.dataHoraPartida DESC", Jogo.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fim", fim);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar jogos por período: " + e.getMessage(), e);
        }
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
        try {
            Jogo jogo = entityManager.find(Jogo.class, id);
            if (jogo != null) {
                entityManager.remove(jogo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover jogo: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se um jogo existe por ID
     */
    public boolean existePorId(Long id) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(j) FROM Jogo j WHERE j.id = :id", Long.class);
            query.setParameter("id", id);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar existência do jogo: " + e.getMessage(), e);
        }
    }

    /**
     * Conta o total de jogos
     */
    public long contarTotal() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(j) FROM Jogo j", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar total de jogos: " + e.getMessage(), e);
        }
    }

    /**
     * Conta jogos por status
     */
    public long contarPorStatus(StatusJogo status) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(j) FROM Jogo j WHERE j.status = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar jogos por status: " + e.getMessage(), e);
        }
    }
}
