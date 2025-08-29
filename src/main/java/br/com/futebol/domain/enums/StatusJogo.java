package br.com.futebol.domain.enums;

/**
 * Enum que define os possíveis status de uma partida de futebol.
 *
 * Basicamente, um jogo pode estar acontecendo agora ou já ter
 * acabado. Esses são os dois estados possíveis no sistema.
 *
 * @author Eu mesmo (desenvolvedor)
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum StatusJogo {

    /**
     * Jogo em andamento.
     *
     * A partida está rolando agora e pode receber
     * atualizações de placar (quando alguém marca gol).
     */
    EM_ANDAMENTO("Em Andamento"),

    /**
     * Jogo encerrado.
     *
     * A partida acabou e não pode mais receber
     * alterações de placar. É tipo "já foi".
     */
    ENCERRADO("Encerrado");

    private final String descricao;

    /**
     * Construtor do enum.
     *
     * Cada status tem uma descrição em português
     * que fica mais bonita na interface.
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
