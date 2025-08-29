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
 * Página para listar todos os jogos
 */
public class TodosJogosPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public TodosJogosPage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Todos os Jogos"));

        // Lista de todos os jogos (dados mockados para demonstração)
        add(new ListView<JogoDTO>("todosJogos", new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                // Retornar lista vazia para demonstração
                return List.of();
            }
        }) {
            @Override
            protected void populateItem(ListItem<JogoDTO> item) {
                // Esta seção será populada quando o serviço estiver funcionando
            }
        });

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
}
