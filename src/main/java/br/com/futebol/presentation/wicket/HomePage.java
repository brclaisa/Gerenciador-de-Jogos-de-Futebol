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
 * <p>Esta página exibe o dashboard principal com links de navegação,
 * estatísticas básicas e listas de jogos em diferentes status.</p>
 *
 * @author Sistema de Futebol
 * @version 1.0.0
 * @since 2024-01-01
 */
public class HomePage extends WebPage {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor da página principal.
     *
     * <p>Inicializa todos os componentes da página, incluindo:
     * - Título da aplicação
     * - Links de navegação para outras páginas
     * - Estatísticas básicas
     * - Listas de jogos (mockadas temporariamente)</p>
     *
     * @param parameters Parâmetros da página (pode ser null)
     */
    public HomePage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Gerenciador de Jogos de Futebol"));

        // Links de navegação
        add(new BookmarkablePageLink<Void>("linkNovoJogo",
                NovoJogoPage.class));
        add(new BookmarkablePageLink<Void>("linkJogosEmAndamento",
                JogosEmAndamentoPage.class));
        add(new BookmarkablePageLink<Void>("linkJogosEncerrados",
                JogosEncerradosPage.class));
        add(new BookmarkablePageLink<Void>("linkTodosJogos",
                TodosJogosPage.class));

        // Estatísticas
        add(new Label("estatisticas",
                "Total: 0 | Em Andamento: 0 | Encerrados: 0"));

        // Lista dos últimos jogos criados (dados mockados para demonstração)
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

        // Lista de jogos em andamento (dados mockados para demonstração)
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
