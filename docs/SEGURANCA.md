# Controle de acesso à gestão

## Permissões

| Ação | Morador sem autenticação | Equipe autenticada |
| --- | --- | --- |
| Consultar categorias, ocorrências e histórico | Permitida | Permitida |
| Registrar ocorrência | Permitida | Permitida |
| Publicar comentário | Permitida | Permitida |
| Editar ocorrência ou mudar situação | Bloqueada | Permitida |
| Excluir ocorrência | Bloqueada | Permitida, respeitando os vínculos |
| Alterar ou excluir comentários/histórico | Bloqueada | Permitida |
| Cadastrar, editar ou excluir categorias | Bloqueada | Permitida |

O sistema ainda não possui contas de moradores ou comprovação de autoria dos relatos. A política adotada permite exclusão somente à equipe responsável, inclusive para registros feitos pelo próprio visitante.

## Configuração no Render

Adicione `ADMIN_TOKEN` em **Environment** com uma chave aleatória de pelo menos 32 caracteres, distinta da senha Oracle. Em instalações pelo Blueprint, `render.yaml` solicita geração automática da chave. Faça um novo deploy após configurar ou substituir o valor.

Sem `ADMIN_TOKEN`, a API inicia com consultas, registros e comentários públicos; todas as operações de gestão ficam bloqueadas. Uma chave configurada com menos de 32 caracteres impede a inicialização, evitando uma configuração fraca.

A chave deve existir somente no ambiente do backend e com a equipe. Não a coloque em variáveis `VITE_*`, no código-fonte, no GitHub ou em URLs. Não é necessária nova variável na Vercel.

## Uso na interface

Nos detalhes de uma ocorrência, abra **Acesso da equipe → Entrar na gestão**, informe a chave e confirme. A API valida o acesso antes de exibir os controles de exclusão e mudança de situação. O botão **Sair da gestão**, a troca de ocorrência e o recarregamento da página removem a chave da memória da interface.

A chave fica no estado em memória do componente. Não é salva em cookies, localStorage, sessionStorage ou parâmetros de URL. A aplicação envia a chave apenas nas chamadas de autenticação e gestão, pelo cabeçalho `Authorization: Bearer ...`. Em produção, frontend e API devem usar HTTPS.

## Proteção na API

Spring Security aplica as regras antes de acessar controllers e banco. Chamadas diretas sem chave ou com chave incorreta retornam `401` em JSON; falta de permissão retorna `403`. O estado do navegador ou um cabeçalho de perfil fornecido pelo cliente não autorizam operações.

`GET /gestao/sessao` valida a chave sem devolvê-la e não cria sessão ou cookie. A autenticação é stateless e não usa HTTP Basic. CORS autoriza o cabeçalho Authorization apenas para as origens configuradas em `CORS_ORIGINS`. As restrições existentes de integridade referencial continuam produzindo `409` para exclusões com vínculos.

## Validação e limites

Os 21 testes de integração passam com os filtros reais de segurança: 11 testes das regras da API, nove testes de permissões e um teste de gestão bloqueada sem chave configurada. As chaves nos testes são exclusivamente fixtures e não são credenciais de produção.

A regressão em navegador com Oracle FIAP realizou nove verificações: bloqueio de chamadas diretas, ausência de controles para visitante, comentário público, rejeição de chave inválida, autenticação da equipe sem armazenamento persistente, mudança de situação, saída, recarregamento e exclusão autorizada. Os registros temporários foram removidos. Evidência: `evidencias/validacao-seguranca.json`.

Uma chave compartilhada identifica a equipe, sem identificar individualmente cada gestor. Contas individuais, permissões por pessoa e moderação de comentários continuam sendo evoluções. Para revogar uma chave, substitua `ADMIN_TOKEN` no backend e faça novo deploy; sair da interface remove somente a cópia local em memória.
