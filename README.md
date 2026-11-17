# Bairro em Dia — Domain Driven Design Using Java

API CitizenTech para moradores registrarem e acompanharem buracos, falhas de iluminação pública e descarte irregular. Java 17, Spring Boot 3.5.7, Maven Wrapper 3.9.11, Oracle JDBC, ModelMapper e Bean Validation.

Três entidades: Categoria, Ocorrencia e Atualizacao; relações 1:N, CRUD completo e rotas aninhadas. Persistência com DAO, DataSource por construtor e PreparedStatement.

## Integrantes e contato

| Nome | RM | Turma | E-mail |
| --- | --- | --- | --- |
| Vinicius de Oliveira | 561862 | 1TDSPW | rm561862@fiap.com.br |
| Robison Custodio Junior | 572866 | 1TDSPW | rm572866@fiap.com.br |
| Lucas Fernando Silva | 564848 | 1TDSPW | rm564848@fiap.com.br |
| Thiago Shimizu Sodré dos Santos | 572867 | 1TDSPW | rm572867@fiap.com.br |
| Arthur Mendes Garcia | 571703 | 1TDSPW | rm571703@fiap.com.br |

Representante: Arthur Mendes Garcia — rm571703@fiap.com.br.

## Links de entrega

- Front End na Vercel: https://bairro-em-dia-frontend-gs-2026-pygm.vercel.app
- API no Render: https://bairro-em-dia-java-gs-2026.onrender.com
- GitHub Front End: https://github.com/proc0x41/bairro-em-dia-frontend-gs-2026
- GitHub Java: https://github.com/proc0x41/bairro-em-dia-java-gs-2026

URLs de publicação atualizadas em 17/11/2026. O resultado dos testes de disponibilidade está no relatório de conferência.

## Executar a partir da raiz deste ZIP

Requisito: JDK 17 e acesso à internet para as dependências na primeira execução.

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Linux/macOS: dê permissão de execução a mvnw e use ./mvnw. O perfil padrão local usa H2 em arquivo e três categorias iniciais. Porta 8080, alterável por PORT. O banco local é uma facilidade de desenvolvimento; o banco exigido na entrega é Oracle FIAP.

Para Oracle, execute database/00-api-oracle.sql apenas em schema novo, sem os objetos correspondentes. Os demais scripts são alternativas de modelo ampliado e não devem ser executados em sequência sobre o schema mínimo. Configure SPRING_PROFILES_ACTIVE=oracle, DB_URL, DB_USERNAME, DB_PASSWORD e CORS_ORIGINS no ambiente; o Spring não lê .env automaticamente. O arquivo .env.example lista os nomes. Credenciais não acompanham o ZIP.

Também é possível usar .\iniciar-oracle.ps1 -Usuario SEU_USUARIO -Porta 8080, que solicita a senha sem exibi-la. Não execute novamente o DDL em um schema já inicializado.

## Acesso da equipe

Configure ADMIN_TOKEN no backend com uma chave aleatória de pelo menos 32 caracteres. Consultar, registrar e comentar são públicos; editar, excluir e administrar categorias exige Bearer. Sem chave configurada, a gestão responde 401. Na interface, use Acesso da equipe → Entrar na gestão. A chave fica apenas na memória da página. Não use a senha do Oracle como chave nem configure segredos em variáveis VITE_*. Veja ACESSO-AVALIACAO.txt.

## Endpoints e regras

CRUD completo em /categorias, /ocorrencias e /atualizacoes: GET/POST na coleção; GET/PUT/DELETE em /{id}. GET e POST em /ocorrencias/{id}/atualizacoes. Saúde: /actuator/health.

Respostas: 201 com Location, 200, 204; erros JSON 400, 401, 403, 404, 405, 409, 415 e 500 genérico. Nova ocorrência começa ABERTA. Fluxo obrigatório: ABERTA → EM_ANALISE → EM_ANDAMENTO → RESOLVIDA. Duplicidade ativa por categoria/endereço/bairro gera 409. Alteração de status grava histórico na mesma transação; FKs impedem exclusão de pais com filhos.

## Estrutura e documentação

- src/main/java: model, dao, dto, service, controller, exception, handler e config.
- src/test/java: testes de integração HTTP, JDBC, regras e segurança.
- database/00-api-oracle.sql: tabelas, sequences, FKs e categorias iniciais.
- docs/Java-Documentacao-Tecnica.pdf: arquitetura, diagramas, endpoints, exemplos, validação e deploy.
- docs/postman_collection.json: coleção de avaliação; configure baseUrl e adminToken.
- Dockerfile e render.yaml: implantação Docker no Render com raiz do repositório em ponto (.).

## Publicação e integração

A API utiliza a variável PORT do Render e o health check /actuator/health. Configure Oracle, CORS_ORIGINS com a origem https://bairro-em-dia-frontend-gs-2026-pygm.vercel.app e ADMIN_TOKEN no serviço. O Front recebe VITE_API_URL=https://bairro-em-dia-java-gs-2026.onrender.com antes do build. Detalhes em docs/DEPLOY.md.

## Verificação

Conferência de 17/11/2026: 21 testes de integração Java aprovados (11 de API, 9 de segurança e 1 de gestão desabilitada); TypeScript aprovado com tsc -b --force. A tentativa de build Vite e de navegador nesta sessão encontrou bloqueio de processo/permissão (spawn EPERM). As evidências anteriores de 13/11/2026 registram build e testes de navegador/Oracle; elas não são apresentadas como uma nova execução. Veja docs/CONFERENCIA-ENTREGA.txt e docs/evidencias/validacao-remota-20261117.json para os resultados e limites da verificação remota.

## Uso de IA

Codex/OpenAI foi usado na implementação, documentação e verificações. O grupo deve compreender e revisar a entrega e demonstrar domínio na apresentação presencial. As evidências não comprovam revisão humana ou participação individual. Consulte docs/USO-IA.md.
