> Registro histórico de 13/11/2026. A conferência atual de 17/11/2026, incluindo URLs, está em CONFERENCIA-ENTREGA.txt. As referências a publicação pendente abaixo descrevem a situação naquela data.

# Validação da API no Oracle FIAP — 13/11/2026

Conexão JDBC autenticada no Oracle Database 19c em `oracle.fiap.com.br`, porta 1521, SID `orcl`. A senha foi usada apenas nas variáveis de ambiente dos processos de validação; não foi escrita nos fontes, documentos ou pacotes.

## Objetos da API

O schema possuía objetos de outros trabalhos. Foram criadas somente `TB_CATEGORIA`, `TB_OCORRENCIA` e `TB_ATUALIZACAO`, suas três sequences e dois índices de FK. Os objetos anteriores foram preservados. Três categorias iniciais foram inseridas com IDs gerados pelas sequences.

O script reproduzível é `backend/database/00-api-oracle.sql`, ou `database/00-api-oracle.sql` no repositório/ZIP Java. Deve ser executado uma única vez em um schema sem esses objetos. Os scripts do modelo ampliado são alternativas e não devem ser combinados com este DDL na mesma base.

## Testes executados

- 32 verificações HTTP na API local com perfil `oracle`: saúde, categorias com acentuação, CRUD das três entidades, Location no cadastro, histórico, duplicidade, transições de status, integridade referencial, validação, IDs ausentes, métodos não suportados e CORS.
- Imagem Docker multi-stage compilada e executada localmente; as mesmas 32 verificações HTTP passaram com o container conectado ao Oracle FIAP.
- Sete verificações da interface compilada em Microsoft Edge: cadastro real, comentário, mudança de status e rota dinâmica nas resoluções 390×844, 768×1024 e 1440×900, sem overflow ou exceções JavaScript.
- Os arquivos do build Vite foram servidos por interceptação local no navegador de teste. As chamadas HTTP à API e a persistência no Oracle foram reais; essa execução não constitui publicação na Vercel.
- Somente os registros temporários criados pelos testes foram removidos ao final. As categorias iniciais foram preservadas.

## Evidências

- `evidencias/validacao-oracle.json`: verificações da API, com métodos, rotas e status.
- `evidencias/validacao-docker-oracle.json`: 32 verificações da API Docker conectada ao Oracle.
- `evidencias/validacao-frontend-oracle.json`: resultados e condições da verificação da interface.
- `evidencias/oracle-registro-sucesso.png` e `evidencias/oracle-ocorrencia-detalhe.png`: capturas locais da integração real.

## Execução e limites

A API de validação foi executada em `http://localhost:18081`, com a senha no ambiente do processo. O helper `backend/iniciar-oracle.ps1` permite iniciar novamente solicitando a senha de forma oculta. O arquivo `.env.example` contém somente exemplos e não é carregado pelo Spring Boot.

Ainda é necessário publicar no Render, configurar suas variáveis de ambiente e confirmar que o Oracle FIAP aceita conexões originadas do serviço remoto. A validação local não comprova esse acesso nem comprova a integração publicada.
