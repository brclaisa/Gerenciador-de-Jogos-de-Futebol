package br.com.futebol.infrastructure.rest;

import br.com.futebol.application.dto.AtualizacaoPlacarDTO;
import br.com.futebol.application.dto.JogoDTO;
import br.com.futebol.application.service.JogoService;
import br.com.futebol.domain.enums.StatusJogo;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador REST que expõe as operações de jogos via API.
 * 
 * Aqui ficam os endpoints HTTP que outras aplicações podem
 * usar pra criar, listar e gerenciar jogos de futebol.
 */
@Path("/jogos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JogoRestController {

    private static final Logger logger = LoggerFactory.getLogger(JogoRestController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Inject
    private JogoService jogoService;

    /**
     * POST /jogos - Criar um novo jogo.
     * 
     * Endpoint que recebe os dados de um jogo e cria
     * no sistema. Valida os dados antes de salvar.
     */
    @POST
    public Response criarJogo(@Valid JogoDTO jogoDTO) {
        try {
            logger.info("Recebida requisição para criar jogo: {} vs {}", jogoDTO.getTimeA(), jogoDTO.getTimeB());

            JogoDTO jogoCriado = jogoService.criarJogo(jogoDTO);

            return Response.status(Response.Status.CREATED)
                    .entity(jogoCriado)
                    .build();

        } catch (Exception e) {
            logger.error("Erro ao criar jogo", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao criar jogo: " + e.getMessage())
                    .build();
        }
    }

    /**
     * GET /jogos - Listar jogos (com filtros opcionais).
     * 
     * Endpoint que retorna a lista de jogos. Pode filtrar
     * por status ou período de datas.
     */
    @GET
    public Response listarJogos(
            @QueryParam("status") String status,
            @QueryParam("dataInicio") String dataInicio,
            @QueryParam("dataFim") String dataFim) {

        try {
            List<JogoDTO> jogos;

            if (status != null && !status.isEmpty()) {
                try {
                    StatusJogo statusJogo = StatusJogo.valueOf(status.toUpperCase());
                    jogos = jogoService.listarPorStatus(statusJogo);
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Status inválido: " + status)
                            .build();
                }
            } else if (dataInicio != null && dataFim != null) {
                try {
                    LocalDateTime inicio = LocalDateTime.parse(dataInicio, FORMATTER);
                    LocalDateTime fim = LocalDateTime.parse(dataFim, FORMATTER);
                    jogos = jogoService.listarPorPeriodo(inicio, fim);
                } catch (Exception e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Formato de data inválido. Use: yyyy-MM-dd'T'HH:mm:ss")
                            .build();
                }
            } else {
                jogos = jogoService.listarTodos();
            }

            return Response.ok(jogos).build();

        } catch (Exception e) {
            logger.error("Erro ao listar jogos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * GET /jogos/{id} - Buscar jogo por ID
     */
    @GET
    @Path("/{id}")
    public Response buscarJogo(@PathParam("id") Long id) {
        try {
            return jogoService.buscarPorId(id)
                    .map(jogo -> Response.ok(jogo).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("Jogo não encontrado com ID: " + id)
                            .build());

        } catch (Exception e) {
            logger.error("Erro ao buscar jogo com ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * PUT /jogos/{id}/placar - Atualizar placar
     */
    @PUT
    @Path("/{id}/placar")
    public Response atualizarPlacar(
            @PathParam("id") Long id,
            @Valid AtualizacaoPlacarDTO placarDTO) {

        try {
            logger.info("Recebida requisição para atualizar placar do jogo {}: {} x {}",
                    id, placarDTO.getPlacarA(), placarDTO.getPlacarB());

            JogoDTO jogoAtualizado = jogoService.atualizarPlacar(id, placarDTO);

            return Response.ok(jogoAtualizado).build();

        } catch (IllegalArgumentException e) {
            logger.warn("Jogo não encontrado: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Jogo não encontrado com ID: " + id)
                    .build();
        } catch (IllegalStateException e) {
            logger.warn("Operação inválida para jogo {}: {}", id, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar placar do jogo: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * PUT /jogos/{id}/status - Alterar status
     */
    @PUT
    @Path("/{id}/status")
    public Response alterarStatus(
            @PathParam("id") Long id,
            @QueryParam("status") String novoStatus) {

        try {
            if (novoStatus == null || novoStatus.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Parâmetro 'status' é obrigatório")
                        .build();
            }

            StatusJogo status;
            try {
                status = StatusJogo.valueOf(novoStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Status inválido: " + novoStatus)
                        .build();
            }

            JogoDTO jogoAtualizado;
            if (StatusJogo.ENCERRADO.equals(status)) {
                jogoAtualizado = jogoService.encerrarJogo(id);
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Apenas é possível alterar para status ENCERRADO")
                        .build();
            }

            return Response.ok(jogoAtualizado).build();

        } catch (IllegalArgumentException e) {
            logger.warn("Jogo não encontrado: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Jogo não encontrado com ID: " + id)
                    .build();
        } catch (IllegalStateException e) {
            logger.warn("Operação inválida para jogo {}: {}", id, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Erro ao alterar status do jogo: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * DELETE /jogos/{id} - Remover jogo
     */
    @DELETE
    @Path("/{id}")
    public Response removerJogo(@PathParam("id") Long id) {
        try {
            jogoService.removerJogo(id);
            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            logger.warn("Jogo não encontrado para remoção: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Jogo não encontrado com ID: " + id)
                    .build();
        } catch (Exception e) {
            logger.error("Erro ao remover jogo: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * GET /jogos/em-andamento - Listar jogos em andamento
     */
    @GET
    @Path("/em-andamento")
    public Response listarEmAndamento() {
        try {
            List<JogoDTO> jogos = jogoService.listarEmAndamento();
            return Response.ok(jogos).build();
        } catch (Exception e) {
            logger.error("Erro ao listar jogos em andamento", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * GET /jogos/encerrados - Listar jogos encerrados
     */
    @GET
    @Path("/encerrados")
    public Response listarEncerrados() {
        try {
            List<JogoDTO> jogos = jogoService.listarEncerrados();
            return Response.ok(jogos).build();
        } catch (Exception e) {
            logger.error("Erro ao listar jogos encerrados", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }

    /**
     * GET /jogos/estatisticas - Obter estatísticas
     */
    @GET
    @Path("/estatisticas")
    @Produces(MediaType.TEXT_PLAIN)
    public Response obterEstatisticas() {
        try {
            String estatisticas = jogoService.obterEstatisticas();
            return Response.ok(estatisticas).build();
        } catch (Exception e) {
            logger.error("Erro ao obter estatísticas", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno do servidor")
                    .build();
        }
    }
}
