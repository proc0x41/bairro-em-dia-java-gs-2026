# Bairro em Dia — Domain Driven Design Using Java

API de zeladoria urbana com Spring Boot 3.5.7, Java 17 e Maven Wrapper. Três entidades: Categoria, Ocorrencia e Atualizacao. DAOs usam DataSource via construtor e PreparedStatement; DTOs com Bean Validation são convertidos por ModelMapper. Não utiliza JPA, Hibernate ou Spring Data.

## Executar e testar

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Perfil padrão: `local`, com H2 em arquivo e três categorias iniciais. Porta padrão 8080, alterável por `PORT`. Para Oracle, configurar `SPRING_PROFILES_ACTIVE=oracle`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `CORS_ORIGINS`. Execute o SQL Oracle incluído antes de iniciar o perfil Oracle. Credenciais não devem ser incluídas no projeto. `.env.example` é uma referência e não é carregado automaticamente pelo Spring Boot.

## Endpoints

CRUD completo: `/categorias`, `/ocorrencias`, `/atualizacoes`. Rotas de coleção: GET e POST. Rotas `/{id}`: GET, PUT e DELETE. Histórico: GET/POST `/ocorrencias/{id}/atualizacoes`. Saúde: GET `/actuator/health`.

Sucesso: 201 + Location, 200 e 204. Erros padronizados: 400, 404, 405, 409 e 500 genérico. CORS permite apenas origens configuradas.

Repositório Java: https://github.com/proc0x41/bairro-em-dia-java-gs-2026

Repositório do Front End integrado: https://github.com/proc0x41/bairro-em-dia-frontend-gs-2026

## Entrega e links

| Nome completo | RM | Turma | E-mail institucional |
| --- | --- | --- | --- |
| Vinicius de Oliveira | 561862 | 1TDSPW | rm561862@fiap.com.br |
| Robison Custodio Junior | 572866 | 1TDSPW | rm572866@fiap.com.br |
| Lucas Fernando Silva | 564848 | 1TDSPW | rm564848@fiap.com.br |
| Thiago Shimizu Sodré dos Santos | 572867 | 1TDSPW | rm572867@fiap.com.br |
| Arthur Mendes Garcia | 571703 | 1TDSPW | rm571703@fiap.com.br |

Representante e contato principal: **Arthur Mendes Garcia — rm571703@fiap.com.br**.

- API Render: **PENDENTE**, publicação ainda não realizada.
- Vídeo YouTube não listado, até 5 minutos: **PENDENTE**, exige gravação real do grupo.
- SQL de tabelas e sequences: incluído na pasta `database` do ZIP Java.
- Documentação técnica: `docs/Java-Documentacao-Tecnica.pdf` no ZIP Java.
- Deploy: Dockerfile multi-stage e instruções em `docs/DEPLOY.md` na entrega.

Testes locais: 11 testes de integração com banco H2 aprovados. Oracle FIAP e Docker/Render ainda precisam de verificação no ambiente de entrega.

Uso de IA: Codex/OpenAI para implementação, documentação e verificação local. Revisão e domínio técnico pelos alunos pendentes.

## Publicação no Render

O repositório Java foi criado separadamente por solicitação do grupo. No Render, conecte este repositório, selecione runtime Docker e Root Directory `.`. O Dockerfile e o `render.yaml` estão na raiz. Configure Oracle e CORS por variáveis de ambiente; nunca inclua credenciais no código.

Como alternativa sem conexão Git, compile uma imagem local com o Dockerfile, envie ao seu registro e use Existing Image no Render. O guia `docs/DEPLOY.md` descreve as opções. O PDF continua exigindo o ZIP Java, a API publicada, a documentação e o vídeo.
