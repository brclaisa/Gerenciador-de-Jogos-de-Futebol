package br.com.futebol.presentation.wicket;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.RuntimeConfigurationType;

/**
 * Classe principal que configura toda a aplicação web Wicket.
 * 
 * Aqui defino as rotas (URLs) e configurações gerais
 * da interface web.
 */
public class WicketApplication extends WebApplication {

    /**
     * Construtor padrão.
     * 
     * Chama o construtor da classe pai (WebApplication).
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

        // Configuração de injeção de dependência (removida para simplificação)

        // Configurar as rotas (URLs) das páginas
        mountPage("/", HomePage.class);
        mountPage("/novo-jogo", NovoJogoPage.class);
        mountPage("/jogos/em-andamento", JogosEmAndamentoPage.class);
        mountPage("/jogos/encerrados", JogosEncerradosPage.class);
        mountPage("/jogos/todos", TodosJogosPage.class);
        mountPage("/jogos/{id}/editar", EditarJogoPage.class);
        mountPage("/jogos/{id}/placar", AtualizarPlacarPage.class);
        mountPage("/jogos/{id}/encerrar", EncerrarJogoPage.class);

        // Configurações adicionais do Wicket
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);

        // Configurar debug (só em desenvolvimento)
        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            getDebugSettings().setDevelopmentUtilitiesEnabled(true);
        }
    }

    /**
     * Define qual é a página inicial da aplicação.
     * 
     * Quando alguém acessa a raiz do site (/), essa
     * página é mostrada.
     */
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }
}
