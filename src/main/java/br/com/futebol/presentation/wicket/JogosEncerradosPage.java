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
 * Página que mostra os jogos que já acabaram.
 * 
 * Por enquanto é só uma demonstração com dados mockados.
 * Na implementação real, listaria todos os jogos com status "encerrado".
 */
public class JogosEncerradosPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public JogosEncerradosPage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Jogos Encerrados"));

        // Lista de jogos encerrados (por enquanto vazia pra demonstração)
        add(new ListView<JogoDTO>("jogosEncerrados", new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                // Por enquanto retorna lista vazia
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
