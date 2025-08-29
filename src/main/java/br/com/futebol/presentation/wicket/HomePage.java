package br.com.futebol.presentation.wicket;

import br.com.futebol.application.dto.JogoDTO;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * Página principal da aplicação de gerenciamento de jogos de futebol.
 *
 * É tipo o "centro de controle" da aplicação. Aqui o usuário
 * vê um resumo geral e pode navegar para outras funcionalidades.
 *
 * @author Eu mesmo (desenvolvedor)
 * @version 1.0.0
 * @since 2024-01-01
 */
public class HomePage extends WebPage {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor da página principal.
     *
     * Inicializa todos os componentes da página: título,
     * links de navegação, estatísticas e listas de jogos.
     * Por enquanto as listas estão vazias (mockadas).
     *
     * @param parameters Parâmetros da página (pode ser null)
     */
    public HomePage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Gerenciador de Jogos de Futebol"));

        // Links de navegação para outras páginas
        add(new BookmarkablePageLink<Void>("linkNovoJogo",
                NovoJogoPage.class));
        add(new BookmarkablePageLink<Void>("linkJogosEmAndamento",
                JogosEmAndamentoPage.class));
        add(new BookmarkablePageLink<Void>("linkJogosEncerrados",
                JogosEncerradosPage.class));
        add(new BookmarkablePageLink<Void>("linkTodosJogos",
                TodosJogosPage.class));

        // Estatísticas básicas (por enquanto fixas)
        add(new Label("estatisticas",
                "Total: 0 | Em Andamento: 0 | Encerrados: 0"));

        // Lista dos últimos jogos criados (dados mockados por enquanto)
        add(new ListView<JogoDTO>("ultimosJogos",
                new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                // Retornar lista vazia para demonstração
                return List.of();
            }
        }) {
            @Override
            protected void populateItem(final ListItem<JogoDTO> item) {
                // Esta seção será populada quando o serviço estiver funcionando
            }
        });

        // Lista de jogos em andamento (dados mockados por enquanto)
        add(new ListView<JogoDTO>("jogosEmAndamento",
                new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                // Retornar lista vazia para demonstração
                return List.of();
            }
        }) {
            @Override
            protected void populateItem(final ListItem<JogoDTO> item) {
                // Esta seção será populada quando o serviço estiver funcionando
            }
        });
    }
}
