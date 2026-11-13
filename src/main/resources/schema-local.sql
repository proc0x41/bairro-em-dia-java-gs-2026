-- Inicialização idempotente exclusiva do perfil local.
CREATE TABLE IF NOT EXISTS tb_categoria (cd_categoria NUMBER(19) PRIMARY KEY, nm_categoria VARCHAR2(80) NOT NULL UNIQUE, ds_categoria VARCHAR2(500) NOT NULL, nr_prazo_dias NUMBER(3) NOT NULL CHECK (nr_prazo_dias BETWEEN 1 AND 90));
CREATE SEQUENCE IF NOT EXISTS sq_tb_categoria START WITH 100 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS tb_equipe (cd_equipe NUMBER(19) PRIMARY KEY, nm_equipe VARCHAR2(100) NOT NULL UNIQUE, ds_especialidade VARCHAR2(120) NOT NULL);
CREATE SEQUENCE IF NOT EXISTS sq_tb_equipe START WITH 100 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS tb_ocorrencia (cd_ocorrencia NUMBER(19) PRIMARY KEY, nm_titulo VARCHAR2(120) NOT NULL, ds_descricao VARCHAR2(2000) NOT NULL, ds_endereco VARCHAR2(250) NOT NULL, nm_bairro VARCHAR2(80) NOT NULL, cd_categoria NUMBER(19) NOT NULL REFERENCES tb_categoria(cd_categoria), ds_prioridade VARCHAR2(10) NOT NULL CHECK (ds_prioridade IN ('BAIXA','MEDIA','ALTA')), ds_status VARCHAR2(20) NOT NULL CHECK (ds_status IN ('ABERTA','EM_ANALISE','EM_ANDAMENTO','RESOLVIDA')), dt_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);
CREATE SEQUENCE IF NOT EXISTS sq_tb_ocorrencia START WITH 100 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS tb_atualizacao (cd_atualizacao NUMBER(19) PRIMARY KEY, cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia), ds_mensagem VARCHAR2(1000) NOT NULL, nm_autor VARCHAR2(100) NOT NULL, dt_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);
CREATE SEQUENCE IF NOT EXISTS sq_tb_atualizacao START WITH 100 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS tb_ocorrencia_equipe (cd_ocorrencia_equipe NUMBER(19) PRIMARY KEY, cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia), cd_equipe NUMBER(19) NOT NULL REFERENCES tb_equipe(cd_equipe), dt_atribuicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT uk_ocorrencia_equipe UNIQUE (cd_ocorrencia, cd_equipe));
CREATE SEQUENCE IF NOT EXISTS sq_tb_ocorrencia_equipe START WITH 100 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS tb_anexo (cd_anexo NUMBER(19) PRIMARY KEY, cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia), ds_url VARCHAR2(500) NOT NULL, ds_legenda VARCHAR2(200) NOT NULL);
CREATE SEQUENCE IF NOT EXISTS sq_tb_anexo START WITH 100 INCREMENT BY 1;
