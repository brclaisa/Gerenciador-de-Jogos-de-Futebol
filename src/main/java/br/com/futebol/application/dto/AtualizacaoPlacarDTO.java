package br.com.futebol.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO usado quando precisamos atualizar o placar de um jogo.
 * 
 * Basicamente, quando o juiz marca um gol ou alguém quer corrigir
 * o placar, usamos essa classe para passar os novos valores.
 * 
 * @author Laisa Brc
 * @version 1.0.0
 * @since 2024-01-01
 */
public final class AtualizacaoPlacarDTO {

    /** Placar do primeiro time (Time A). */
    @NotNull(message = "Placar do time A é obrigatório")
    @Min(value = 0, message = "Placar do time A não pode ser negativo")
    private Integer placarA;

    /** Placar do segundo time (Time B). */
    @NotNull(message = "Placar do time B é obrigatório")
    @Min(value = 0, message = "Placar do time B não pode ser negativo")
    private Integer placarB;

    /**
     * Construtor padrão.
     * 
     * Preciso disso pro Spring/Jakarta funcionar direito.
     * Sem isso dá erro de serialização.
     */
    public AtualizacaoPlacarDTO() {
        // Construtor padrão necessário para frameworks
    }

    /**
     * Construtor com parâmetros obrigatórios.
     * 
     * Uso esse quando já sei os placares e quero criar
     * o DTO direto, sem usar setters.
     * 
     * @param placarA Placar do time A
     * @param placarB Placar do time B
     */
    public AtualizacaoPlacarDTO(final Integer placarA, final Integer placarB) {
        this.placarA = placarA;
        this.placarB = placarB;
    }

    // Getters e Setters - padrão Java Bean

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

    @Override
    public String toString() {
        return "AtualizacaoPlacarDTO{" +
                "placarA=" + placarA +
                ", placarB=" + placarB +
                '}';
    }
}
