package br.com.futebol.presentation.wicket;

import br.com.futebol.application.dto.AtualizacaoPlacarDTO;
import br.com.futebol.application.dto.JogoDTO;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 * Página para atualizar placar de um jogo
 */
public class AtualizarPlacarPage extends WebPage {

    private static final long serialVersionUID = 1L;

    private AtualizacaoPlacarDTO placarDTO;
    private JogoDTO jogo;

    public AtualizarPlacarPage(final PageParameters parameters) {
        super(parameters);

        Long jogoId = parameters.get("id").toLong();

        // Para demonstração, criar dados mockados
        jogo = new JogoDTO();
        jogo.setId(jogoId);
        jogo.setTimeA("Time A");
        jogo.setTimeB("Time B");

        placarDTO = new AtualizacaoPlacarDTO(0, 0);

        // Título da página
        add(new Label("titulo", "Atualizar Placar"));
        add(new Label("jogoInfo", jogo.getTimeA() + " vs " + jogo.getTimeB()));

        // Formulário
        Form<AtualizacaoPlacarDTO> form = new Form<AtualizacaoPlacarDTO>("formAtualizarPlacar") {
            @Override
            protected void onSubmit() {
                try {
                    // Em uma implementação real, aqui seria chamado o serviço
                    // jogoService.atualizarPlacar(jogoId, placarDTO);

                    // Redirecionar para a página principal com mensagem de sucesso
                    setResponsePage(HomePage.class, new PageParameters().add("mensagem", "Placar atualizado com sucesso!"));

                } catch (Exception e) {
                    error("Erro ao atualizar placar: " + e.getMessage());
                }
            }
        };

        // Campos do formulário
        form.add(new NumberTextField<Integer>("placarA", new PropertyModel<>(placarDTO, "placarA"))
                .setRequired(true)
                .add(RangeValidator.minimum(0)));

        form.add(new NumberTextField<Integer>("placarB", new PropertyModel<>(placarDTO, "placarB"))
                .setRequired(true)
                .add(RangeValidator.minimum(0)));

        // Botões
        form.add(new BookmarkablePageLink<Void>("linkCancelar", HomePage.class));

        add(form);

        // Painel de feedback
        add(new FeedbackPanel("feedback"));

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
}
