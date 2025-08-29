package br.com.futebol.application.dto;

import br.com.futebol.domain.enums.StatusJogo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * DTO principal que representa um jogo de futebol.
 *
 * Uso essa classe para passar dados de jogos entre as diferentes
 * partes da aplicação. É tipo um envelope que carrega todas as
 * informações importantes de uma partida.
 *
 * @author Eu mesmo (desenvolvedor)
 * @version 1.0.0
 * @since 2024-01-01
 */
public class JogoDTO {

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraPartida;

    /** Data e hora de criação do registro. */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;

    /** Data e hora da última atualização. */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAtualizacao;

    /**
     * Construtor padrão.
     * 
     * Preciso disso pro Spring/Jakarta funcionar. Sem isso
     * dá erro na hora de deserializar JSON ou usar reflexão.
     */
    public JogoDTO() {
        // Construtor padrão necessário para frameworks
    }

    /**
     * Construtor com parâmetros obrigatórios.
     *
     * Uso esse quando já sei os dados básicos do jogo e
     * quero criar o DTO direto, sem ficar usando setters.
     *
     * @param timeA Nome do primeiro time
     * @param timeB Nome do segundo time
     * @param dataHoraPartida Data e hora da partida
     */
    public JogoDTO(final String timeA, final String timeB,
            final LocalDateTime dataHoraPartida) {
        this.timeA = timeA;
        this.timeB = timeB;
        this.dataHoraPartida = dataHoraPartida;
    }

    // Getters e Setters - padrão Java Bean

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
     * Define a data de criação do registro.
     *
     * @param dataCriacao Data de criação
     */
    public void setDataCriacao(final LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /**
     * Obtém a data da última atualização.
     *
     * @return Data da última atualização
     */
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    /**
     * Define a data da última atualização.
     *
     * @param dataAtualizacao Data da última atualização
     */
    public void setDataAtualizacao(final LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    /**
     * Obtém o resultado formatado da partida.
     *
     * Método útil pra mostrar quem ganhou ou se deu empate.
     * Uso bastante na interface pra exibir o resultado de forma
     * amigável pro usuário.
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
}
