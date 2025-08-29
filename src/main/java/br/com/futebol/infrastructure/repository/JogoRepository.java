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
     * Construtor para inicializar EntityManager quando CDI falhar
     */
    public JogoRepository() {
        inicializarEntityManager();
    }

    /**
     * Inicializa o EntityManager manualmente se necessário
     */
    private void inicializarEntityManager() {
        if (entityManager == null) {
            try {
                // Criar EntityManager manualmente usando Hibernate
                jakarta.persistence.EntityManagerFactory emf = 
                    jakarta.persistence.Persistence.createEntityManagerFactory("futebolPU");
                this.entityManager = emf.createEntityManager();
                System.out.println("EntityManager inicializado manualmente - CDI não funcionou");
            } catch (Exception e) {
                System.err.println("Erro ao inicializar EntityManager: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Salva um novo jogo
     */
    public Jogo salvar(Jogo jogo) {
        inicializarEntityManager();
        if (jogo.getId() == null) {
            entityManager.persist(jogo);
        } else {
            jogo = entityManager.merge(jogo);
        }
        return jogo;
    }

    /**
     * Atualiza um jogo existente
     */
    public Jogo atualizar(Jogo jogo) {
        inicializarEntityManager();
        return entityManager.merge(jogo);
    }

    /**
     * Busca um jogo por ID
     */
    public Optional<Jogo> buscarPorId(Long id) {
        Jogo jogo = entityManager.find(Jogo.class, id);
        return Optional.ofNullable(jogo);
    }

    /**
     * Lista todos os jogos
     */
    public List<Jogo> listarTodos() {
        inicializarEntityManager();
        TypedQuery<Jogo> query = entityManager.createQuery(
            "SELECT j FROM Jogo j ORDER BY j.dataHoraPartida DESC", Jogo.class);
        return query.getResultList();
    }

    /**
     * Lista jogos por status
     */
    public List<Jogo> listarPorStatus(StatusJogo status) {
        TypedQuery<Jogo> query = entityManager.createQuery(
            "SELECT j FROM Jogo j WHERE j.status = :status ORDER BY j.dataHoraPartida DESC", Jogo.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Lista jogos por período
     */
    public List<Jogo> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        TypedQuery<Jogo> query = entityManager.createQuery(
            "SELECT j FROM Jogo j WHERE j.dataHoraPartida BETWEEN :inicio AND :fim ORDER BY j.dataHoraPartida DESC", Jogo.class);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        return query.getResultList();
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
        Jogo jogo = entityManager.find(Jogo.class, id);
        if (jogo != null) {
            entityManager.remove(jogo);
        }
    }

    /**
     * Verifica se um jogo existe por ID
     */
    public boolean existePorId(Long id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(j) FROM Jogo j WHERE j.id = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    /**
     * Conta o total de jogos
     */
    public long contarTotal() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(j) FROM Jogo j", Long.class);
        return query.getSingleResult();
    }

    /**
     * Conta jogos por status
     */
    public long contarPorStatus(StatusJogo status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(j) FROM Jogo j WHERE j.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }
}
