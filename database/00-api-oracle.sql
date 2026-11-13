-- Schema mínimo da API Java. Execute somente se estes objetos não existirem.
-- Não remove nem modifica objetos de outros trabalhos no mesmo usuário Oracle.
CREATE TABLE tb_categoria (
    cd_categoria NUMBER(19) PRIMARY KEY,
    nm_categoria VARCHAR2(80) NOT NULL UNIQUE,
    ds_categoria VARCHAR2(500) NOT NULL,
    nr_prazo_dias NUMBER(3) NOT NULL CHECK (nr_prazo_dias BETWEEN 1 AND 90)
);
CREATE SEQUENCE sq_tb_categoria START WITH 100 INCREMENT BY 1 NOCACHE;

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
CREATE SEQUENCE sq_tb_ocorrencia START WITH 100 INCREMENT BY 1 NOCACHE;

CREATE TABLE tb_atualizacao (
    cd_atualizacao NUMBER(19) PRIMARY KEY,
    cd_ocorrencia NUMBER(19) NOT NULL REFERENCES tb_ocorrencia(cd_ocorrencia),
    ds_mensagem VARCHAR2(1000) NOT NULL,
    nm_autor VARCHAR2(100) NOT NULL,
    dt_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE SEQUENCE sq_tb_atualizacao START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE INDEX ix_ocorrencia_categoria ON tb_ocorrencia(cd_categoria);
CREATE INDEX ix_atualizacao_ocorrencia ON tb_atualizacao(cd_ocorrencia);

-- Carga inicial opcional, executada uma única vez após o DDL acima.
INSERT INTO tb_categoria VALUES (sq_tb_categoria.NEXTVAL, 'Buraco na via', 'Buracos, desníveis e danos no pavimento.', 15);
INSERT INTO tb_categoria VALUES (sq_tb_categoria.NEXTVAL, 'Iluminação pública', 'Lâmpadas apagadas, piscando ou acesas durante o dia.', 7);
INSERT INTO tb_categoria VALUES (sq_tb_categoria.NEXTVAL, 'Descarte irregular', 'Resíduos e entulho descartados em espaços públicos.', 5);
COMMIT;
