package br.com.futebol.presentation.wicket;

import br.com.futebol.application.dto.JogoDTO;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Página para encerrar um jogo
 */
public class EncerrarJogoPage extends WebPage {

    private static final long serialVersionUID = 1L;

    private JogoDTO jogo;

    public EncerrarJogoPage(final PageParameters parameters) {
        super(parameters);

        Long jogoId = parameters.get("id").toLong();

        // Para demonstração, criar dados mockados
        jogo = new JogoDTO();
        jogo.setId(jogoId);
        jogo.setTimeA("Time A");
        jogo.setTimeB("Time B");
        jogo.setPlacarA(0);
        jogo.setPlacarB(0);

        // Título da página
        add(new Label("titulo", "Encerrar Jogo"));
        add(new Label("jogoInfo", jogo.getTimeA() + " vs " + jogo.getTimeB()));
        add(new Label("placarAtual", "Placar atual: " + jogo.getPlacarA() + " x " + jogo.getPlacarB()));

        // Formulário de confirmação
        Form<Void> form = new Form<Void>("formEncerrarJogo") {
            @Override
            protected void onSubmit() {
                try {
                    // Em uma implementação real, aqui seria chamado o serviço
                    // jogoService.encerrarJogo(jogo.getId());

                    // Redirecionar para a página principal com mensagem de sucesso
                    setResponsePage(HomePage.class, new PageParameters().add("mensagem", "Jogo encerrado com sucesso!"));

                } catch (Exception e) {
                    error("Erro ao encerrar jogo: " + e.getMessage());
                }
            }
        };

        // Botões
        form.add(new BookmarkablePageLink<Void>("linkCancelar", HomePage.class));

        add(form);

        // Painel de feedback
        add(new FeedbackPanel("feedback"));

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
}
