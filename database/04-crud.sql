-- UPDATE com WHERE: corrigir prioridade de um registro específico.
UPDATE tb_ocorrencia SET ds_prioridade = 'ALTA' WHERE cd_ocorrencia = 1;
-- DELETE com WHERE: remover somente um metadado de evidência ilustrativa.
DELETE FROM tb_anexo WHERE cd_anexo = 6;
COMMIT;
-- Os 60 INSERTs ocorrem antes da exclusão: ao final do CRUD ficam 59 registros.
