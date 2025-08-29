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
import java.util.ArrayList;
import java.sql.*;

/**
 * Página que mostra apenas os jogos que estão acontecendo agora.
 * 
 * Aqui o usuário vê só os jogos em andamento, tipo um "painel
 * ao vivo" das partidas que estão rolando.
 */
public class JogosEmAndamentoPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public JogosEmAndamentoPage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Jogos em Andamento"));

        // Tentar obter o serviço via CDI (injeção de dependência)
        final Object jogoService = obterJogoService();

        // Lista de jogos em andamento - busca dados reais do banco
        add(new ListView<JogoDTO>("jogosEmAndamento", new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                try {
                    if (jogoService instanceof JogoService) {
                        return ((JogoService) jogoService).listarEmAndamento();
                    } else {
                        // Usar serviço simplificado
                        return carregarJogosEmAndamentoSimplificado();
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar jogos em andamento: " + e.getMessage());
                    return carregarJogosEmAndamentoSimplificado();
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
    private Object obterJogoService() {
        try {
            JogoService tempService = CDIProvider.getInstance(JogoService.class);
            if (tempService == null) {
                System.err.println("CDI não funcionando, usando serviço simplificado");
                // Fallback: usar serviço simplificado
                return "SIMPLIFICADO";
            } else {
                System.err.println("JogoService obtido via CDI com sucesso");
                return tempService;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter JogoService: " + e.getMessage());
            // Último recurso: usar serviço simplificado
            return "SIMPLIFICADO";
        }
    }
    
    /**
     * Carrega jogos em andamento usando JDBC direto quando o CDI não funciona
     */
    private List<JogoDTO> carregarJogosEmAndamentoSimplificado() {
        System.err.println("Carregando jogos em andamento via JDBC direto");
        try {
            // Usar JDBC direto para conectar ao PostgreSQL
            String url = "jdbc:postgresql://localhost:5432/futebol_db";
            String user = "futebol_user";
            String password = "futebol_pass";
            
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String sql = "SELECT id, time_a, time_b, placar_a, placar_b, status, data_hora_partida, data_criacao FROM jogos WHERE status = 'EM_ANDAMENTO' ORDER BY data_criacao DESC";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    List<JogoDTO> jogos = new ArrayList<>();
                    while (rs.next()) {
                        JogoDTO jogo = new JogoDTO();
                        jogo.setId(rs.getLong("id"));
                        jogo.setTimeA(rs.getString("time_a"));
                        jogo.setTimeB(rs.getString("time_b"));
                        jogo.setPlacarA(rs.getInt("placar_a"));
                        jogo.setPlacarB(rs.getInt("placar_b"));
                        jogo.setStatus(br.com.futebol.domain.enums.StatusJogo.valueOf(rs.getString("status")));
                        
                        // Converter data
                        Timestamp dataPartida = rs.getTimestamp("data_hora_partida");
                        if (dataPartida != null) {
                            jogo.setDataHoraPartida(dataPartida.toLocalDateTime());
                        }
                        
                        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
                        if (dataCriacao != null) {
                            jogo.setDataCriacao(dataCriacao.toLocalDateTime());
                        }
                        
                        jogos.add(jogo);
                    }
                    
                    System.err.println("Jogos em andamento carregados via JDBC: " + jogos.size());
                    return jogos;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar jogos em andamento via JDBC: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ArrayList<>();
    }
}
