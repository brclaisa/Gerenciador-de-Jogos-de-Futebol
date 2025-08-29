package br.com.futebol.domain.enums;

/**
 * Enum que representa o status de uma partida de futebol.
 *
 * <p>Define os possíveis estados que um jogo pode ter durante
 * seu ciclo de vida na aplicação.</p>
 *
 * @author Sistema de Futebol
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum StatusJogo {

    /**
     * Jogo em andamento.
     *
     * <p>Indica que a partida está sendo disputada e pode
     * receber atualizações de placar.</p>
     */
    EM_ANDAMENTO("Em Andamento"),

    /**
     * Jogo encerrado.
     *
     * <p>Indica que a partida foi finalizada e não pode
     * mais receber alterações de placar.</p>
     */
    ENCERRADO("Encerrado");

    private final String descricao;

    /**
     * Construtor do enum.
     *
     * @param descricao Descrição legível do status
     */
    StatusJogo(final String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtém a descrição legível do status.
     *
     * @return Descrição do status em português
     */
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
