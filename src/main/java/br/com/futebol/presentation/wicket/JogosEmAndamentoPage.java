package br.com.futebol.presentation.wicket;

import br.com.futebol.application.dto.JogoDTO;
import br.com.futebol.application.service.JogoService;
import br.com.futebol.infrastructure.util.CDIProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * Página para listar jogos em andamento
 */
public class JogosEmAndamentoPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public JogosEmAndamentoPage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Jogos em Andamento"));

        // Obter instância do JogoService via CDI
        final JogoService jogoService = obterJogoService();

        // Lista de jogos em andamento (dados reais do banco)
        add(new ListView<JogoDTO>("jogosEmAndamento", new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                try {
                    return jogoService.listarEmAndamento();
                } catch (Exception e) {
                    System.err.println("Erro ao carregar jogos em andamento: " + e.getMessage());
                    return List.of();
                }
            }
        }) {
            @Override
            protected void populateItem(ListItem<JogoDTO> item) {
                JogoDTO jogo = item.getModelObject();
                
                // Nome dos times
                item.add(new Label("timeA", jogo.getTimeA()));
                item.add(new Label("timeB", jogo.getTimeB()));
                
                // Placar
                item.add(new Label("placar", jogo.getPlacarA() + " x " + jogo.getPlacarB()));
                
                // Data da partida
                String dataFormatada = jogo.getDataHoraPartida() != null ? 
                    jogo.getDataHoraPartida().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : 
                    "N/A";
                item.add(new Label("dataPartida", dataFormatada));
            }
        });

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
    
    /**
     * Obtém uma instância do JogoService
     */
    private JogoService obterJogoService() {
        try {
            JogoService tempService = CDIProvider.getInstance(JogoService.class);
            if (tempService == null) {
                System.err.println("CDI não funcionando, criando instância manual");
                // Fallback: criar instância manualmente
                return new JogoService();
            } else {
                System.err.println("JogoService obtido via CDI com sucesso");
                return tempService;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter JogoService: " + e.getMessage());
            // Último recurso: criar instância básica
            return new JogoService();
        }
    }
}
