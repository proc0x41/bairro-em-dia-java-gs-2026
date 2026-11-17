# Publicação — Vercel, Render e Oracle FIAP

URLs informadas e verificadas por HTTP em 17/11/2026:

- Front: https://bairro-em-dia-frontend-gs-2026-pygm.vercel.app
- API: https://bairro-em-dia-java-gs-2026.onrender.com

O relatório CONFERENCIA-ENTREGA.txt distingue testes locais, evidências antigas
e verificações remotas. GET de categorias/ocorrências e preflight CORS passaram.
Não houve novo deploy nesta sessão nem acesso ao painel de configuração.

## Java / Render

Repositório: https://github.com/proc0x41/bairro-em-dia-java-gs-2026.
Runtime Docker, raiz ponto (.), Dockerfile ./Dockerfile, contexto ponto (.).
Health check /actuator/health. O serviço respeita PORT.
Configure SPRING_PROFILES_ACTIVE=oracle, DB_URL, DB_USERNAME, DB_PASSWORD,
CORS_ORIGINS e ADMIN_TOKEN no painel. A chave deve ter ao menos 32 caracteres.
Inclua a origem https://bairro-em-dia-frontend-gs-2026-pygm.vercel.app no CORS.

Em um schema Oracle novo, execute database/00-api-oracle.sql uma única vez.
Não execute os outros scripts sobre o schema mínimo; eles são modelos alternativos.
Credenciais não são entregues no código. O Spring Boot não carrega .env sozinho.
Oracle local e container foram validados em 13/11/2026; isso não comprova o
perfil/banco que está efetivamente configurado no serviço remoto.

## Front / Vercel

Repositório: https://github.com/proc0x41/bairro-em-dia-frontend-gs-2026.
Raiz ponto (.), instalação npm ci, build npm run build, saída dist.
VITE_API_URL=https://bairro-em-dia-java-gs-2026.onrender.com; vercel.json contém rewrite da SPA.
Após alterar variáveis Vite, gere novo build/deploy. Nunca coloque credenciais
ou ADMIN_TOKEN em variáveis VITE_*.

## Avaliação

Use a coleção postman_collection.json com baseUrl e adminToken apropriados.
Para gestão remota, solicite a chave ao representante pelo canal privado.
ACESSO-AVALIACAO.txt explica a execução local com chave definida pelo avaliador.
