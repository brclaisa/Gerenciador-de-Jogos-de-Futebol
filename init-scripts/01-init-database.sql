-- Script de inicialização do banco de dados para o Gerenciador de Jogos de Futebol
-- Executado automaticamente pelo container PostgreSQL

-- Criar extensões necessárias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Criar schema se não existir
CREATE SCHEMA IF NOT EXISTS public;

-- Configurar timezone
SET timezone = 'America/Sao_Paulo';

-- Criar tabela de jogos
CREATE TABLE IF NOT EXISTS jogos (
    id BIGSERIAL PRIMARY KEY,
    time_a VARCHAR(100) NOT NULL,
    time_b VARCHAR(100) NOT NULL,
    placar_a INTEGER NOT NULL DEFAULT 0,
    placar_b INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
    data_hora_partida TIMESTAMP NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP
);

-- Criar índices para performance
CREATE INDEX IF NOT EXISTS idx_jogos_status ON jogos(status);
CREATE INDEX IF NOT EXISTS idx_jogos_data_hora ON jogos(data_hora_partida);
CREATE INDEX IF NOT EXISTS idx_jogos_data_criacao ON jogos(data_criacao);
CREATE INDEX IF NOT EXISTS idx_jogos_times ON jogos(time_a, time_b);

-- Criar constraint de validação para placares
ALTER TABLE jogos ADD CONSTRAINT chk_placar_nao_negativo 
    CHECK (placar_a >= 0 AND placar_b >= 0);

-- Criar constraint de validação para status
ALTER TABLE jogos ADD CONSTRAINT chk_status_valido 
    CHECK (status IN ('EM_ANDAMENTO', 'ENCERRADO'));

-- Criar constraint de validação para data da partida
ALTER TABLE jogos ADD CONSTRAINT chk_data_partida_futura 
    CHECK (data_hora_partida > CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Criar função para atualizar timestamp de atualização
CREATE OR REPLACE FUNCTION atualizar_timestamp_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criar trigger para atualizar timestamp automaticamente
DROP TRIGGER IF EXISTS trigger_atualizar_timestamp ON jogos;
CREATE TRIGGER trigger_atualizar_timestamp
    BEFORE UPDATE ON jogos
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_timestamp_atualizacao();

-- Criar função para validar se jogo pode ser atualizado
CREATE OR REPLACE FUNCTION validar_atualizacao_jogo()
RETURNS TRIGGER AS $$
BEGIN
    -- Não permitir atualização de jogo encerrado
    IF OLD.status = 'ENCERRADO' AND NEW.status = 'ENCERRADO' THEN
        RAISE EXCEPTION 'Não é possível atualizar jogo encerrado';
    END IF;
    
    -- Validar se placar não é negativo
    IF NEW.placar_a < 0 OR NEW.placar_b < 0 THEN
        RAISE EXCEPTION 'Placar não pode ser negativo';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criar trigger para validação
DROP TRIGGER IF EXISTS trigger_validar_atualizacao ON jogos;
CREATE TRIGGER trigger_validar_atualizacao
    BEFORE UPDATE ON jogos
    FOR EACH ROW
    EXECUTE FUNCTION validar_atualizacao_jogo();

-- Inserir dados de exemplo (opcional)
INSERT INTO jogos (time_a, time_b, placar_a, placar_b, status, data_hora_partida, data_criacao) VALUES
    ('Flamengo', 'Palmeiras', 0, 0, 'EM_ANDAMENTO', CURRENT_TIMESTAMP + INTERVAL '2 hours', CURRENT_TIMESTAMP),
    ('Corinthians', 'São Paulo', 1, 1, 'EM_ANDAMENTO', CURRENT_TIMESTAMP + INTERVAL '3 hours', CURRENT_TIMESTAMP),
    ('Santos', 'Vasco', 2, 0, 'ENCERRADO', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours'),
    ('Grêmio', 'Internacional', 0, 0, 'EM_ANDAMENTO', CURRENT_TIMESTAMP + INTERVAL '1 hour', CURRENT_TIMESTAMP),
    ('Atlético Mineiro', 'Cruzeiro', 3, 1, 'ENCERRADO', CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP - INTERVAL '7 hours')
ON CONFLICT DO NOTHING;

-- Criar view para estatísticas
CREATE OR REPLACE VIEW estatisticas_jogos AS
SELECT 
    COUNT(*) as total_jogos,
    COUNT(CASE WHEN status = 'EM_ANDAMENTO' THEN 1 END) as jogos_em_andamento,
    COUNT(CASE WHEN status = 'ENCERRADO' THEN 1 END) as jogos_encerrados,
    AVG(CASE WHEN status = 'ENCERRADO' THEN placar_a + placar_b END) as media_gols_por_jogo,
    MAX(CASE WHEN status = 'ENCERRADO' THEN placar_a + placar_b END) as maior_placar,
    MIN(CASE WHEN status = 'ENCERRADO' THEN placar_a + placar_b END) as menor_placar
FROM jogos;

-- Criar view para jogos em andamento
CREATE OR REPLACE VIEW jogos_em_andamento AS
SELECT 
    id,
    time_a,
    time_b,
    placar_a,
    placar_b,
    data_hora_partida,
    data_criacao,
    EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - data_criacao))/3600 as horas_decorridas
FROM jogos 
WHERE status = 'EM_ANDAMENTO'
ORDER BY data_hora_partida;

-- Criar view para jogos encerrados
CREATE OR REPLACE VIEW jogos_encerrados AS
SELECT 
    id,
    time_a,
    time_b,
    placar_a,
    placar_b,
    data_hora_partida,
    data_criacao,
    data_atualizacao,
    CASE 
        WHEN placar_a > placar_b THEN time_a || ' venceu por ' || placar_a || ' x ' || placar_b
        WHEN placar_b > placar_a THEN time_b || ' venceu por ' || placar_b || ' x ' || placar_a
        ELSE 'Empate: ' || placar_a || ' x ' || placar_b
    END as resultado
FROM jogos 
WHERE status = 'ENCERRADO'
ORDER BY data_atualizacao DESC;

-- Conceder permissões
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO futebol_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO futebol_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO futebol_user;

-- Configurar configurações de performance
ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';
ALTER SYSTEM SET pg_stat_statements.track = 'all';
ALTER SYSTEM SET pg_stat_statements.max = 10000;

-- Aplicar configurações
SELECT pg_reload_conf();

-- Mensagem de sucesso
DO $$
BEGIN
    RAISE NOTICE 'Banco de dados inicializado com sucesso!';
    RAISE NOTICE 'Tabela jogos criada com % registros de exemplo', (SELECT COUNT(*) FROM jogos);
    RAISE NOTICE 'Views e funções criadas com sucesso';
END $$;
