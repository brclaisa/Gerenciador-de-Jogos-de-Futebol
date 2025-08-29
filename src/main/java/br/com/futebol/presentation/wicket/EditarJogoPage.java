package br.com.futebol.presentation.wicket;

import br.com.futebol.application.dto.JogoDTO;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Página para editar um jogo existente
 */
public class EditarJogoPage extends WebPage {

    private static final long serialVersionUID = 1L;

    private JogoDTO jogo;

    public EditarJogoPage(final PageParameters parameters) {
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
        add(new Label("titulo", "Editar Jogo"));
        add(new Label("jogoId", "ID: " + jogo.getId()));

        // Formulário
        Form<JogoDTO> form = new Form<JogoDTO>("formEditarJogo") {
            @Override
            protected void onSubmit() {
                try {
                    // Em uma implementação real, aqui seria chamado o serviço
                    // jogoService.atualizarJogo(jogo);

                    // Redirecionar para a página principal com mensagem de sucesso
                    setResponsePage(HomePage.class, new PageParameters().add("mensagem", "Jogo atualizado com sucesso!"));

                } catch (Exception e) {
                    error("Erro ao atualizar jogo: " + e.getMessage());
                }
            }
        };

        // Campos do formulário (somente leitura para demonstração)
        form.add(new TextField<String>("timeA", new PropertyModel<>(jogo, "timeA"))
                .setRequired(true)
                .add(StringValidator.lengthBetween(2, 100))
                .setEnabled(false)); // Desabilitado para demonstração

        form.add(new TextField<String>("timeB", new PropertyModel<>(jogo, "timeB"))
                .setRequired(true)
                .add(StringValidator.lengthBetween(2, 100))
                .setEnabled(false)); // Desabilitado para demonstração

        form.add(new Label("placar", jogo.getPlacarA() + " x " + jogo.getPlacarB()));
        form.add(new Label("status", "Em Andamento"));
        add(new Label("dataHora", "Data/Hora: " + java.time.LocalDateTime.now()));

        // Botões
        form.add(new BookmarkablePageLink<Void>("linkCancelar", HomePage.class));

        add(form);

        // Painel de feedback
        add(new FeedbackPanel("feedback"));

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
}
