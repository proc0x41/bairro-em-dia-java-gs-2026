# Publicação — Vercel, Render e Oracle FIAP

Os deploys serão realizados posteriormente pelo usuário. Os repositórios estão separados por disciplina:

- Front End: https://github.com/proc0x41/bairro-em-dia-frontend-gs-2026.
- Java: https://github.com/proc0x41/bairro-em-dia-java-gs-2026, criado por solicitação do grupo.

Render e Vercel ainda não foram publicados.

## 1. Oracle

Conexão local validada em 13/11/2026: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl`. O schema mínimo da API já foi inicializado com três tabelas, sequences, índices e categorias. Em um schema novo, execute uma única vez `backend/database/00-api-oracle.sql`, ou `database/00-api-oracle.sql` no ZIP/repositório Java. A senha deve ser fornecida apenas no ambiente ou painel do Render.

Os scripts do modelo ampliado são referências opcionais e criam objetos com nomes iguais aos do schema mínimo. Não os execute sobre a base já inicializada. Consulte `VALIDACAO-ORACLE.md` para resultados de CRUD, regras, interface e Docker com Oracle.

## 2. GitHub

O PDF exige GitHub para Front End nas páginas 63 e 70. Apenas a pasta local `frontend/` corresponde ao repositório publicado, com `package.json` e `src/` na raiz. O Front End exige no mínimo 5 commits por integrante e 15 no total (25 ao considerar cinco integrantes).

Java pede código em ZIP e API Docker no Render, sem exigir GitHub (páginas 47–55); seu repositório adicional facilita o deploy e foi solicitado pelo grupo. Engenharia pede PDF, backlog Jira/Trello e vídeo (página 91), sem repositório separado. A entrega de Banco é mantida por outra pessoa; os scripts necessários ao Java permanecem em backend/database.

## 3. Render

### Repositório Java separado

1. Criar Web Service conectado a `proc0x41/bairro-em-dia-java-gs-2026`.
2. Runtime **Docker**, Root Directory **`.`**, Dockerfile `./Dockerfile`, contexto `.`.
3. Definir `SPRING_PROFILES_ACTIVE=oracle`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `CORS_ORIGINS` no painel.
4. Health check: `/actuator/health`. O serviço respeita a variável `PORT`.
5. Verificar Oracle e CRUD. O `render.yaml` da raiz do repositório Java descreve essa configuração.

### Alternativa com imagem Docker pronta

O Render aceita **Existing Image**: uma imagem Docker já compilada, sem repositório Git do Java. Na pasta local `backend/`, substitua SEU_USUARIO pelo namespace do seu registro de imagens:

```powershell
docker build --platform linux/amd64 -t SEU_USUARIO/bairro-em-dia-api:1.0.0 .
docker login
docker push SEU_USUARIO/bairro-em-dia-api:1.0.0
```

1. Criar Web Service no Render e selecionar **Existing Image**. Informar a URL da imagem publicada e credenciais de leitura se o registro for privado.
2. A imagem usa o Dockerfile multi-stage entregue, com Maven na compilação e JRE 17 na execução. Não inclui credenciais Oracle.
3. Configurar `SPRING_PROFILES_ACTIVE=oracle`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `CORS_ORIGINS`. Não colocar valores no Dockerfile ou no YAML.
4. O Render fornece `PORT`; a aplicação respeita esse valor.
5. Health check: `/actuator/health`.
6. Verificar conexão com Oracle e CRUD via Postman. Se o banco não aceitar conexões do Render, resolver o acesso com a FIAP; não substituir silenciosamente o Oracle por H2 na entrega.

O `render.yaml` da pasta agregadora local é a referência alternativa com runtime `image`; o da pasta `backend/`, publicado no repositório Java, usa runtime `docker`. Nenhum pertence ao repositório de Front End. Planos e limites devem ser conferidos no serviço; não houve contratação ou publicação.

## 4. Vercel

1. Importar `proc0x41/bairro-em-dia-frontend-gs-2026`, Root Directory **`.`** (raiz do repositório), Framework Preset Vite.
2. Install: `npm ci`; Build: `npm run build`; Output Directory: `dist`.
3. Configurar `VITE_API_URL=https://SUA-API.onrender.com`, sem barra final.
4. Publicar. O `vercel.json` reescreve as rotas da SPA para `index.html`.
5. Voltar ao Render e incluir a URL **exata** da Vercel em `CORS_ORIGINS` (separada por vírgulas das demais origens).
6. Depois de alterar variáveis do Vite, realizar novo build/deploy: elas são incorporadas no bundle.

Nunca coloque `DB_PASSWORD` ou outro segredo em variáveis `VITE_*`: elas são públicas no navegador.

## 5. Conferência remota

- Abrir Home e `/ocorrencias` em janela anônima.
- Registrar um problema no Front End e verificar POST 201 + Location.
- Consultar o mesmo protocolo na API e na rota dinâmica da Vercel.
- Publicar uma atualização, avançar status e verificar histórico.
- Demonstrar 400, 404, 405 e 409, sem stack trace ou SQL no corpo.
- Colar as URLs reais nos READMEs e nos PDFs. Gravar os vídeos com voz dos alunos.

Referências oficiais consultadas em 13/11/2026: [imagem Docker pronta no Render](https://render.com/docs/deploying-an-image), [configuração Render](https://render.com/docs/blueprint-spec), [Vite na Vercel](https://vercel.com/docs/frameworks/frontend/vite) e [variáveis Vite](https://vite.dev/guide/env-and-mode).
