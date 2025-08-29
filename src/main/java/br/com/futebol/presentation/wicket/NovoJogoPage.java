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

import java.time.LocalDateTime;

/**
 * Página para criar um novo jogo
 */
public class NovoJogoPage extends WebPage {

    private static final long serialVersionUID = 1L;

    private JogoDTO jogoDTO;

    public NovoJogoPage(final PageParameters parameters) {
        super(parameters);

        jogoDTO = new JogoDTO();

        // Título da página
        add(new Label("titulo", "Criar Novo Jogo"));

        // Formulário
        Form<JogoDTO> form = new Form<JogoDTO>("formNovoJogo") {
            @Override
            protected void onSubmit() {
                try {
                    // Definir data/hora padrão se não fornecida
                    if (jogoDTO.getDataHoraPartida() == null) {
                        jogoDTO.setDataHoraPartida(LocalDateTime.now().plusHours(1));
                    }

                    // Em uma implementação real, aqui seria chamado o serviço
                    // jogoService.criarJogo(jogoDTO);

                    // Redirecionar para a página principal com mensagem de sucesso
                    setResponsePage(HomePage.class, new PageParameters().add("mensagem", "Jogo criado com sucesso!"));

                } catch (Exception e) {
                    error("Erro ao criar jogo: " + e.getMessage());
                }
            }
        };

        // Campos do formulário
        form.add(new TextField<String>("timeA", new PropertyModel<>(jogoDTO, "timeA"))
                .setRequired(true)
                .add(StringValidator.lengthBetween(2, 100)));

        form.add(new TextField<String>("timeB", new PropertyModel<>(jogoDTO, "timeB"))
                .setRequired(true)
                .add(StringValidator.lengthBetween(2, 100)));

        // Campo para data e hora da partida
        form.add(new TextField<LocalDateTime>("dataHoraPartida", new PropertyModel<>(jogoDTO, "dataHoraPartida"))
                .setRequired(true));

        // Botões
        form.add(new BookmarkablePageLink<Void>("linkCancelar", HomePage.class));

        add(form);

        // Painel de feedback
        add(new FeedbackPanel("feedback"));

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
}
