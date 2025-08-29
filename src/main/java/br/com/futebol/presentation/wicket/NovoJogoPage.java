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
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.convert.ConversionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

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

        // Campo para data e hora da partida com conversor personalizado
        TextField<LocalDateTime> dataHoraField = new TextField<LocalDateTime>("dataHoraPartida", 
                new PropertyModel<>(jogoDTO, "dataHoraPartida")) {
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                if (LocalDateTime.class.isAssignableFrom(type)) {
                    return (IConverter<C>) new LocalDateTimeConverter();
                }
                return super.getConverter(type);
            }
        };
        dataHoraField.setRequired(true);
        form.add(dataHoraField);

        // Botões
        form.add(new BookmarkablePageLink<Void>("linkCancelar", HomePage.class));

        add(form);

        // Painel de feedback
        add(new FeedbackPanel("feedback"));

        // Link de volta
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }

    /**
     * Conversor personalizado para LocalDateTime
     */
    private static class LocalDateTimeConverter extends AbstractConverter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        @Override
        public LocalDateTime convertToObject(String value, Locale locale) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return LocalDateTime.parse(value.trim(), FORMATTER);
            } catch (DateTimeParseException e) {
                throw new ConversionException("Formato de data inválido. Use: dd/MM/yyyy HH:mm", e);
            }
        }

        @Override
        public String convertToString(LocalDateTime value, Locale locale) {
            if (value == null) {
                return "";
            }
            return value.format(FORMATTER);
        }

        @Override
        protected Class<LocalDateTime> getTargetType() {
            return LocalDateTime.class;
        }
    }
}
