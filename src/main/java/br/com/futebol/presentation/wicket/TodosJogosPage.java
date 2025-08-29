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
 * Página que mostra todos os jogos cadastrados no sistema.
 * 
 * Aqui o usuário pode ver todos os jogos, independente do status.
 * É tipo um "catálogo completo" de partidas.
 */
public class TodosJogosPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public TodosJogosPage(final PageParameters parameters) {
        super(parameters);

        // Título da página
        add(new Label("titulo", "Todos os Jogos"));

        // Tentar obter o serviço via CDI (injeção de dependência)
        final Object jogoService = obterJogoService();

        // Lista de todos os jogos - busca dados reais do banco
        add(new ListView<JogoDTO>("todosJogos", new LoadableDetachableModel<List<JogoDTO>>() {
            @Override
            protected List<JogoDTO> load() {
                try {
                    if (jogoService instanceof JogoService) {
                        return ((JogoService) jogoService).listarTodos();
                    } else {
                        // CDI falhou, usar o plano B (JDBC direto)
                        return carregarJogosSimplificado();
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao carregar jogos: " + e.getMessage());
                    // Deu erro, tentar o plano B mesmo
                    return carregarJogosSimplificado();
                }
            }
        }) {
            @Override
            protected void populateItem(ListItem<JogoDTO> item) {
                JogoDTO jogo = item.getModelObject();
                
                // Nome dos times
                item.add(new Label("timeA", jogo.getTimeA()));
                item.add(new Label("timeB", jogo.getTimeB()));
                
                // Placar atual
                item.add(new Label("placar", jogo.getPlacarA() + " x " + jogo.getPlacarB()));
                
                // Status do jogo (em andamento, encerrado, etc)
                item.add(new Label("status", jogo.getStatus().toString()));
                
                // Data e hora da partida
                String dataFormatada = jogo.getDataHoraPartida() != null ? 
                    jogo.getDataHoraPartida().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : 
                    "N/A";
                item.add(new Label("dataPartida", dataFormatada));
            }
        });

        // Botão de voltar pra página inicial
        add(new BookmarkablePageLink<Void>("linkVoltar", HomePage.class));
    }
    
    /**
     * Tenta obter o JogoService via CDI (injeção de dependência).
     * 
     * Se der certo, ótimo! Se der errado, retorna uma string
     * que indica pra usar o plano B (JDBC direto).
     */
    private Object obterJogoService() {
        try {
            JogoService tempService = CDIProvider.getInstance(JogoService.class);
            if (tempService == null) {
                System.err.println("CDI não funcionando, usando serviço simplificado");
                // CDI falhou, marcar pra usar o plano B
                return "SIMPLIFICADO";
            } else {
                System.err.println("JogoService obtido via CDI com sucesso");
                return tempService;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter JogoService: " + e.getMessage());
            // Deu erro, usar o plano B mesmo
            return "SIMPLIFICADO";
        }
    }
    
    /**
     * Plano B: carrega jogos usando JDBC direto quando o CDI não funciona.
     * 
     * É tipo um "backup" que conecta direto no banco sem passar
     * pelo sistema de injeção de dependências.
     */
    private List<JogoDTO> carregarJogosSimplificado() {
        System.err.println("Carregando jogos via JDBC direto");
        try {
            // Conectar direto no PostgreSQL (plano B)
            String url = "jdbc:postgresql://localhost:5432/futebol_db";
            String user = "futebol_user";
            String password = "futebol_pass";
            
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String sql = "SELECT id, time_a, time_b, placar_a, placar_b, status, data_hora_partida, data_criacao FROM jogos ORDER BY data_criacao DESC";
                
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
                    
                    System.err.println("Jogos carregados via JDBC: " + jogos.size());
                    return jogos;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar jogos via JDBC: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ArrayList<>();
    }
}
