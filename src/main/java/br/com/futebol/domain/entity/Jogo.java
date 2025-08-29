package br.com.futebol.domain.entity;

import br.com.futebol.domain.enums.StatusJogo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa uma partida de futebol.
 * 
 * <p>Esta classe encapsula todas as informações de uma partida,
 * incluindo times, placares, status e datas. Implementa regras
 * de negócio relacionadas ao ciclo de vida do jogo.</p>
 * 
 * @author Sistema de Futebol
 * @version 1.0.0
 * @since 2024-01-01
 */
public final class Jogo {

    /** Identificador único do jogo. */
    private Long id;

    /** Nome do primeiro time (Time A). */
    @NotBlank(message = "Nome do time A é obrigatório")
    private String timeA;

    /** Nome do segundo time (Time B). */
    @NotBlank(message = "Nome do time B é obrigatório")
    private String timeB;

    /** Placar atual do time A. */
    @Min(value = 0, message = "Placar do time A não pode ser negativo")
    private Integer placarA = 0;

    /** Placar atual do time B. */
    @Min(value = 0, message = "Placar do time B não pode ser negativo")
    private Integer placarB = 0;

    /** Status atual do jogo. */
    private StatusJogo status = StatusJogo.EM_ANDAMENTO;

    /** Data e hora programada para a partida. */
    @NotNull(message = "Data e hora da partida é obrigatória")
    private LocalDateTime dataHoraPartida;

    /** Data e hora de criação do registro. */
    private LocalDateTime dataCriacao;

    /** Data e hora da última atualização. */
    private LocalDateTime dataAtualizacao;

    /**
     * Construtor padrão.
     * 
     * <p>Inicializa a data de criação com o momento atual.</p>
     */
    public Jogo() {
        this.dataCriacao = LocalDateTime.now();
    }

    /**
     * Construtor com parâmetros obrigatórios.
     * 
     * @param timeA Nome do primeiro time
     * @param timeB Nome do segundo time
     * @param dataHoraPartida Data e hora da partida
     */
    public Jogo(final String timeA, final String timeB, 
            final LocalDateTime dataHoraPartida) {
        this();
        this.timeA = timeA;
        this.timeB = timeB;
        this.dataHoraPartida = dataHoraPartida;
    }

    // Getters e Setters

    /**
     * Obtém o identificador do jogo.
     * 
     * @return ID do jogo
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do jogo.
     * 
     * @param id ID do jogo
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * Obtém o nome do primeiro time.
     * 
     * @return Nome do time A
     */
    public String getTimeA() {
        return timeA;
    }

    /**
     * Define o nome do primeiro time.
     * 
     * @param timeA Nome do time A
     */
    public void setTimeA(final String timeA) {
        this.timeA = timeA;
    }

    /**
     * Obtém o nome do segundo time.
     * 
     * @return Nome do time B
     */
    public String getTimeB() {
        return timeB;
    }

    /**
     * Define o nome do segundo time.
     * 
     * @param timeB Nome do time B
     */
    public void setTimeB(final String timeB) {
        this.timeB = timeB;
    }

    /**
     * Obtém o placar do primeiro time.
     * 
     * @return Placar do time A
     */
    public Integer getPlacarA() {
        return placarA;
    }

    /**
     * Define o placar do primeiro time.
     * 
     * @param placarA Placar do time A
     */
    public void setPlacarA(final Integer placarA) {
        this.placarA = placarA;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Obtém o placar do segundo time.
     * 
     * @return Placar do time B
     */
    public Integer getPlacarB() {
        return placarB;
    }

    /**
     * Define o placar do segundo time.
     * 
     * @param placarB Placar do time B
     */
    public void setPlacarB(final Integer placarB) {
        this.placarB = placarB;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Obtém o status atual do jogo.
     * 
     * @return Status do jogo
     */
    public StatusJogo getStatus() {
        return status;
    }

    /**
     * Define o status do jogo.
     * 
     * @param status Status do jogo
     */
    public void setStatus(final StatusJogo status) {
        this.status = status;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Obtém a data e hora da partida.
     * 
     * @return Data e hora da partida
     */
    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    /**
     * Define a data e hora da partida.
     * 
     * @param dataHoraPartida Data e hora da partida
     */
    public void setDataHoraPartida(final LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    /**
     * Obtém a data de criação do registro.
     * 
     * @return Data de criação
     */
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    /**
     * Obtém a data da última atualização.
     * 
     * @return Data da última atualização
     */
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    // Métodos de negócio

    /**
     * Atualiza o placar do jogo.
     * 
     * @param placarA Novo placar do time A
     * @param placarB Novo placar do time B
     */
    public void atualizarPlacar(final Integer placarA, final Integer placarB) {
        this.placarA = placarA;
        this.placarB = placarB;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Encerra o jogo.
     * 
     * <p>Altera o status para ENCERRADO e atualiza a data de modificação.</p>
     */
    public void encerrarJogo() {
        this.status = StatusJogo.ENCERRADO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Verifica se o jogo está em andamento.
     * 
     * @return true se o jogo estiver em andamento
     */
    public boolean isEmAndamento() {
        return StatusJogo.EM_ANDAMENTO.equals(this.status);
    }

    /**
     * Verifica se o jogo está encerrado.
     * 
     * @return true se o jogo estiver encerrado
     */
    public boolean isEncerrado() {
        return StatusJogo.ENCERRADO.equals(this.status);
    }

    /**
     * Obtém o resultado formatado da partida.
     * 
     * @return String com o resultado da partida
     */
    public String getResultado() {
        if (placarA > placarB) {
            return timeA + " venceu por " + placarA + " x " + placarB;
        } else if (placarB > placarA) {
            return timeB + " venceu por " + placarB + " x " + placarA;
        } else {
            return "Empate: " + placarA + " x " + placarB;
        }
    }

    // Equals e HashCode

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Jogo jogo = (Jogo) o;
        return Objects.equals(id, jogo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Jogo{" +
                "id=" + id +
                ", timeA='" + timeA + '\'' +
                ", timeB='" + timeB + '\'' +
                ", placarA=" + placarA +
                ", placarB=" + placarB +
                ", status=" + status +
                ", dataHoraPartida=" + dataHoraPartida +
                '}';
    }
}
