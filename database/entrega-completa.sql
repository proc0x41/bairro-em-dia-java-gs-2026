-- Bairro em Dia | Oracle FIAP | executar em schema vazio
-- Convenção tb_, cd_, nm_, ds_, dt_ e sq_tb_.

CREATE TABLE tb_categoria (
    cd_categoria NUMBER(19) PRIMARY KEY,
    nm_categoria VARCHAR2(80) NOT NULL UNIQUE,
    ds_categoria VARCHAR2(500) NOT NULL,
    nr_prazo_dias NUMBER(3) NOT NULL CHECK (nr_prazo_dias BETWEEN 1 AND 90)
);
COMMENT ON TABLE tb_categoria IS 'Tipos de problemas urbanos e prazo alvo em dias.';
CREATE SEQUENCE sq_tb_categoria START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_equipe (
    cd_equipe NUMBER(19) PRIMARY KEY,
    nm_equipe VARCHAR2(100) NOT NULL UNIQUE,
    ds_especialidade VARCHAR2(120) NOT NULL
);
COMMENT ON TABLE tb_equipe IS 'Equipes responsáveis pela zeladoria urbana.';
CREATE SEQUENCE sq_tb_equipe START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_ocorrencia (
    cd_ocorrencia NUMBER(19) PRIMARY KEY,
    nm_titulo VARCHAR2(120) NOT NULL,
    ds_descricao VARCHAR2(2000) NOT NULL,
    ds_endereco VARCHAR2(250) NOT NULL,
    nm_bairro VARCHAR2(80) NOT NULL,
    cd_categoria NUMBER(19) NOT NULL REFERENCES tb_categoria(cd_categoria),
    ds_prioridade VARCHAR2(10) NOT NULL CHECK (ds_prioridade IN ('BAIXA','MEDIA','ALTA')),
    ds_status VARCHAR2(20) NOT NULL CHECK (ds_status IN ('ABERTA','EM_ANALISE','EM_ANDAMENTO','RESOLVIDA')),
    dt_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
COMMENT ON TABLE tb_ocorrencia IS 'Registro colaborativo de problema urbano, sem dados pessoais do morador.';
CREATE SEQUENCE sq_tb_ocorrencia START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_atualizacao (
    cd_atualizacao NUMBER(19) PRIMARY KEY,
    cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia),
    ds_mensagem VARCHAR2(1000) NOT NULL,
    nm_autor VARCHAR2(100) NOT NULL,
    dt_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
COMMENT ON TABLE tb_atualizacao IS 'Histórico público de acompanhamento de uma ocorrência.';
CREATE SEQUENCE sq_tb_atualizacao START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_ocorrencia_equipe (
    cd_ocorrencia_equipe NUMBER(19) PRIMARY KEY,
    cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia),
    cd_equipe NUMBER(19) NOT NULL REFERENCES tb_equipe(cd_equipe),
    dt_atribuicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_ocorrencia_equipe UNIQUE (cd_ocorrencia,
    cd_equipe)
);
COMMENT ON TABLE tb_ocorrencia_equipe IS 'Associação N:N entre ocorrências e equipes de atendimento.';
CREATE SEQUENCE sq_tb_ocorrencia_equipe START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_anexo (
    cd_anexo NUMBER(19) PRIMARY KEY,
    cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia),
    ds_url VARCHAR2(500) NOT NULL,
    ds_legenda VARCHAR2(200) NOT NULL
);
COMMENT ON TABLE tb_anexo IS 'Metadados de evidências; upload não faz parte da API desta etapa.';
CREATE SEQUENCE sq_tb_anexo START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE INDEX ix_ocorrencia_categoria ON tb_ocorrencia(cd_categoria);
CREATE INDEX ix_atualizacao_ocorrencia ON tb_atualizacao(cd_ocorrencia);
CREATE INDEX ix_ocorrencia_equipe_equipe ON tb_ocorrencia_equipe(cd_equipe);
CREATE INDEX ix_anexo_ocorrencia ON tb_anexo(cd_ocorrencia);

-- 60 registros sintéticos explícitos; não são dados de campo.
INSERT INTO tb_categoria VALUES (1, 'Buraco na via', 'Buracos, desníveis e danos no pavimento.', 15);
INSERT INTO tb_categoria VALUES (2, 'Iluminação pública', 'Lâmpadas apagadas, piscando ou acesas durante o dia.', 7);
INSERT INTO tb_categoria VALUES (3, 'Descarte irregular', 'Resíduos e entulho descartados em espaços públicos.', 5);
INSERT INTO tb_equipe VALUES (1, 'Conservação viária', 'Buraco na via');
INSERT INTO tb_equipe VALUES (2, 'Iluminação urbana', 'Iluminação pública');
INSERT INTO tb_equipe VALUES (3, 'Limpeza pública', 'Descarte irregular');
INSERT INTO tb_ocorrencia VALUES (1, 'Buraco próximo à faixa de pedestres', 'Registro demonstrativo: Buracos, desníveis e danos no pavimento. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 1, 112', 'Vila Mariana', 1, 'MEDIA', 'ABERTA', TIMESTAMP '2026-11-02 10:00:00');
INSERT INTO tb_ocorrencia VALUES (2, 'Poste apagado no caminho da escola', 'Registro demonstrativo: Lâmpadas apagadas, piscando ou acesas durante o dia. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 2, 124', 'Pinheiros', 2, 'ALTA', 'EM_ANALISE', TIMESTAMP '2026-11-03 10:00:00');
INSERT INTO tb_ocorrencia VALUES (3, 'Entulho acumulado na calçada', 'Registro demonstrativo: Resíduos e entulho descartados em espaços públicos. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 3, 136', 'Bela Vista', 3, 'BAIXA', 'EM_ANDAMENTO', TIMESTAMP '2026-11-04 10:00:00');
INSERT INTO tb_ocorrencia VALUES (4, 'Buraco próximo à faixa de pedestres', 'Registro demonstrativo: Buracos, desníveis e danos no pavimento. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 4, 148', 'Liberdade', 1, 'MEDIA', 'RESOLVIDA', TIMESTAMP '2026-11-05 10:00:00');
INSERT INTO tb_ocorrencia VALUES (5, 'Poste apagado no caminho da escola', 'Registro demonstrativo: Lâmpadas apagadas, piscando ou acesas durante o dia. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 5, 160', 'Mooca', 2, 'ALTA', 'ABERTA', TIMESTAMP '2026-11-06 10:00:00');
INSERT INTO tb_ocorrencia VALUES (6, 'Entulho acumulado na calçada', 'Registro demonstrativo: Resíduos e entulho descartados em espaços públicos. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 6, 172', 'Santana', 3, 'BAIXA', 'EM_ANALISE', TIMESTAMP '2026-11-07 10:00:00');
INSERT INTO tb_ocorrencia VALUES (7, 'Buraco próximo à faixa de pedestres', 'Registro demonstrativo: Buracos, desníveis e danos no pavimento. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 7, 184', 'Vila Mariana', 1, 'MEDIA', 'EM_ANDAMENTO', TIMESTAMP '2026-11-08 10:00:00');
INSERT INTO tb_ocorrencia VALUES (8, 'Poste apagado no caminho da escola', 'Registro demonstrativo: Lâmpadas apagadas, piscando ou acesas durante o dia. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 8, 196', 'Pinheiros', 2, 'ALTA', 'RESOLVIDA', TIMESTAMP '2026-11-09 10:00:00');
INSERT INTO tb_ocorrencia VALUES (9, 'Entulho acumulado na calçada', 'Registro demonstrativo: Resíduos e entulho descartados em espaços públicos. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 9, 208', 'Bela Vista', 3, 'BAIXA', 'ABERTA', TIMESTAMP '2026-11-01 10:00:00');
INSERT INTO tb_ocorrencia VALUES (10, 'Buraco próximo à faixa de pedestres', 'Registro demonstrativo: Buracos, desníveis e danos no pavimento. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 10, 220', 'Liberdade', 1, 'MEDIA', 'EM_ANALISE', TIMESTAMP '2026-11-02 10:00:00');
INSERT INTO tb_ocorrencia VALUES (11, 'Poste apagado no caminho da escola', 'Registro demonstrativo: Lâmpadas apagadas, piscando ou acesas durante o dia. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 11, 232', 'Mooca', 2, 'ALTA', 'EM_ANDAMENTO', TIMESTAMP '2026-11-03 10:00:00');
INSERT INTO tb_ocorrencia VALUES (12, 'Entulho acumulado na calçada', 'Registro demonstrativo: Resíduos e entulho descartados em espaços públicos. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 12, 244', 'Santana', 3, 'BAIXA', 'RESOLVIDA', TIMESTAMP '2026-11-04 10:00:00');
INSERT INTO tb_ocorrencia VALUES (13, 'Buraco próximo à faixa de pedestres', 'Registro demonstrativo: Buracos, desníveis e danos no pavimento. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 13, 256', 'Vila Mariana', 1, 'MEDIA', 'ABERTA', TIMESTAMP '2026-11-05 10:00:00');
INSERT INTO tb_ocorrencia VALUES (14, 'Poste apagado no caminho da escola', 'Registro demonstrativo: Lâmpadas apagadas, piscando ou acesas durante o dia. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 14, 268', 'Pinheiros', 2, 'ALTA', 'EM_ANALISE', TIMESTAMP '2026-11-06 10:00:00');
INSERT INTO tb_ocorrencia VALUES (15, 'Entulho acumulado na calçada', 'Registro demonstrativo: Resíduos e entulho descartados em espaços públicos. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 15, 280', 'Bela Vista', 3, 'BAIXA', 'EM_ANDAMENTO', TIMESTAMP '2026-11-07 10:00:00');
INSERT INTO tb_ocorrencia VALUES (16, 'Buraco próximo à faixa de pedestres', 'Registro demonstrativo: Buracos, desníveis e danos no pavimento. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 16, 292', 'Liberdade', 1, 'MEDIA', 'RESOLVIDA', TIMESTAMP '2026-11-08 10:00:00');
INSERT INTO tb_ocorrencia VALUES (17, 'Poste apagado no caminho da escola', 'Registro demonstrativo: Lâmpadas apagadas, piscando ou acesas durante o dia. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 17, 304', 'Mooca', 2, 'ALTA', 'ABERTA', TIMESTAMP '2026-11-09 10:00:00');
INSERT INTO tb_ocorrencia VALUES (18, 'Entulho acumulado na calçada', 'Registro demonstrativo: Resíduos e entulho descartados em espaços públicos. A situação dificulta a circulação e precisa de vistoria.', 'Rua Exemplo 18, 316', 'Santana', 3, 'BAIXA', 'EM_ANALISE', TIMESTAMP '2026-11-01 10:00:00');
INSERT INTO tb_atualizacao VALUES (1, 1, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (2, 2, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (3, 3, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (4, 4, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (5, 5, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (6, 6, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (7, 7, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (8, 8, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (9, 9, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (10, 10, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (11, 11, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (12, 12, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (13, 13, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (14, 14, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (15, 15, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (16, 16, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (17, 17, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_atualizacao VALUES (18, 18, 'Ocorrência recebida para acompanhamento. Dados demonstrativos.', 'Equipe Bairro em Dia', TIMESTAMP '2026-11-10 12:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (1, 1, 1, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (2, 2, 2, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (3, 3, 3, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (4, 4, 1, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (5, 5, 2, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (6, 6, 3, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (7, 7, 1, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (8, 8, 2, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (9, 9, 3, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (10, 10, 1, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (11, 11, 2, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_ocorrencia_equipe VALUES (12, 12, 3, TIMESTAMP '2026-11-10 13:00:00');
INSERT INTO tb_anexo VALUES (1, 1, 'https://example.com/evidencia-1.jpg', 'Endereço ilustrativo: arquivo não hospedado.');
INSERT INTO tb_anexo VALUES (2, 2, 'https://example.com/evidencia-2.jpg', 'Endereço ilustrativo: arquivo não hospedado.');
INSERT INTO tb_anexo VALUES (3, 3, 'https://example.com/evidencia-3.jpg', 'Endereço ilustrativo: arquivo não hospedado.');
INSERT INTO tb_anexo VALUES (4, 4, 'https://example.com/evidencia-4.jpg', 'Endereço ilustrativo: arquivo não hospedado.');
INSERT INTO tb_anexo VALUES (5, 5, 'https://example.com/evidencia-5.jpg', 'Endereço ilustrativo: arquivo não hospedado.');
INSERT INTO tb_anexo VALUES (6, 6, 'https://example.com/evidencia-6.jpg', 'Endereço ilustrativo: arquivo não hospedado.');
COMMIT;

-- 01. Quais ocorrências abertas exigem vistoria primeiro? WHERE, ORDER BY.
SELECT cd_ocorrencia, nm_titulo, ds_prioridade FROM tb_ocorrencia WHERE ds_status = 'ABERTA' ORDER BY CASE ds_prioridade WHEN 'ALTA' THEN 1 WHEN 'MEDIA' THEN 2 ELSE 3 END, dt_criacao;
-- 02. Quantos dias cada ocorrência está aberta? Cálculo no SELECT.
SELECT cd_ocorrencia, nm_titulo, TRUNC(SYSDATE - CAST(dt_criacao AS DATE)) AS dias_desde_registro FROM tb_ocorrencia ORDER BY dias_desde_registro DESC;
-- 03. Quantos problemas existem por categoria? INNER JOIN, COUNT, GROUP BY.
SELECT c.nm_categoria, COUNT(o.cd_ocorrencia) AS quantidade FROM tb_categoria c INNER JOIN tb_ocorrencia o ON o.cd_categoria = c.cd_categoria GROUP BY c.nm_categoria;
-- 04. Quais bairros concentram pelo menos três ocorrências? HAVING.
SELECT nm_bairro, COUNT(*) AS total FROM tb_ocorrencia GROUP BY nm_bairro HAVING COUNT(*) >= 3 ORDER BY total DESC;
-- 05. Como se distribuem prazo alvo e esforço acumulado por bairro? SUM, AVG, MAX, MIN.
SELECT o.nm_bairro, SUM(c.nr_prazo_dias) AS soma_prazos_alvo, AVG(c.nr_prazo_dias) AS prazo_medio, MAX(c.nr_prazo_dias) AS maior_prazo, MIN(c.nr_prazo_dias) AS menor_prazo FROM tb_ocorrencia o INNER JOIN tb_categoria c ON c.cd_categoria = o.cd_categoria GROUP BY o.nm_bairro;
-- 06. Quais ocorrências têm categoria com prazo acima da média? Subconsulta.
SELECT cd_ocorrencia, nm_titulo FROM tb_ocorrencia WHERE cd_categoria IN (SELECT cd_categoria FROM tb_categoria WHERE nr_prazo_dias > (SELECT AVG(nr_prazo_dias) FROM tb_categoria));
-- 07. Quantos anexos existem para cada ocorrência, incluindo as sem evidência? LEFT JOIN.
SELECT o.cd_ocorrencia, o.nm_titulo, COUNT(a.cd_anexo) AS total_anexos FROM tb_ocorrencia o LEFT JOIN tb_anexo a ON a.cd_ocorrencia = o.cd_ocorrencia GROUP BY o.cd_ocorrencia, o.nm_titulo ORDER BY o.cd_ocorrencia;
-- 08. Todas as equipes possuem atribuições? RIGHT JOIN.
SELECT e.nm_equipe, COUNT(oe.cd_ocorrencia) AS atribuicoes FROM tb_ocorrencia_equipe oe RIGHT JOIN tb_equipe e ON e.cd_equipe = oe.cd_equipe GROUP BY e.nm_equipe;
-- 09. Qual é o histórico do protocolo 1? INNER JOIN e ordenação temporal.
SELECT o.cd_ocorrencia, a.nm_autor, a.ds_mensagem, a.dt_criacao FROM tb_ocorrencia o INNER JOIN tb_atualizacao a ON a.cd_ocorrencia = o.cd_ocorrencia WHERE o.cd_ocorrencia = 1 ORDER BY a.dt_criacao, a.cd_atualizacao;
-- 10. Qual é a taxa de resolução por bairro? Cálculo, SUM, COUNT.
SELECT nm_bairro, COUNT(*) AS total, SUM(CASE WHEN ds_status = 'RESOLVIDA' THEN 1 ELSE 0 END) AS resolvidas, ROUND(100.0 * SUM(CASE WHEN ds_status = 'RESOLVIDA' THEN 1 ELSE 0 END) / COUNT(*), 2) AS percentual_resolvido FROM tb_ocorrencia GROUP BY nm_bairro ORDER BY percentual_resolvido DESC;

-- UPDATE com WHERE: corrigir prioridade de um registro específico.
UPDATE tb_ocorrencia SET ds_prioridade = 'ALTA' WHERE cd_ocorrencia = 1;
-- DELETE com WHERE: remover somente um metadado de evidência ilustrativa.
DELETE FROM tb_anexo WHERE cd_anexo = 6;
COMMIT;
-- Os 60 INSERTs ocorrem antes da exclusão: ao final do CRUD ficam 59 registros.
