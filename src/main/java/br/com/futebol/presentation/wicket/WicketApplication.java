package br.com.futebol.presentation.wicket;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.RuntimeConfigurationType;

/**
 * Classe principal da aplicacao Wicket
 */
public class WicketApplication extends WebApplication {

    /**
     * Construtor
     */
    public WicketApplication() {
        super();
    }

    /**
     * Configuracao da aplicacao
     */
    @Override
    protected void init() {
        super.init();

        // Configuracao de injecao de dependencia (removida para simplificacao)

        // Configurar paginas
        mountPage("/", HomePage.class);
        mountPage("/novo-jogo", NovoJogoPage.class);
        mountPage("/jogos/em-andamento", JogosEmAndamentoPage.class);
        mountPage("/jogos/encerrados", JogosEncerradosPage.class);
        mountPage("/jogos/todos", TodosJogosPage.class);
        mountPage("/jogos/{id}/editar", EditarJogoPage.class);
        mountPage("/jogos/{id}/placar", AtualizarPlacarPage.class);
        mountPage("/jogos/{id}/encerrar", EncerrarJogoPage.class);

        // Configuracoes adicionais
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);

        // Configurar debug
        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            getDebugSettings().setDevelopmentUtilitiesEnabled(true);
        }
    }

    /**
     * Pagina inicial
     */
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }
}
