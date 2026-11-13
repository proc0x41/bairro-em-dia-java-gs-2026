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
