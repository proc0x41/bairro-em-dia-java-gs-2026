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
