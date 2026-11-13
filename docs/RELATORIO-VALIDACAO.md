# Validação local — 11/11/2026

## Resultados executados

| Verificação | Resultado |
| --- | --- |
| `npm run build` (TypeScript + Vite) | Aprovado |
| `mvnw.cmd test` | 11 testes; 0 falhas; 0 erros |
| Empacotamento Java executável | Aprovado |
| Cadastro real pelo navegador | POST 201, protocolo do banco local e confirmação |
| Publicação de atualização | POST aninhado e atualização no histórico |
| Alteração de situação | PUT e histórico automático refletidos na interface |
| Busca por bairro e ausência de resultados | Aprovadas |
| Nove rotas em 390×844, 768×1024 e 1440×900 | Sem overflow horizontal e com título de página |
| Menu móvel | Abre, navega e fecha ao mudar de rota |
| Formulário inválido | Impede envio, mostra instruções e orienta o foco |
| Contato | Gera download local, sem afirmar envio de e-mail |
| API indisponível | Mensagem de erro e tentativa de repetição |
| Exceções JavaScript durante navegação | Nenhuma observada |

## Testes Java

Os testes em `ApiIntegrationTest.java` percorrem MockMvc → controller → serviço → DAO JDBC → H2, sem mock de persistência. Cada teste usa transação com rollback e banco de memória isolado dos dados do aplicativo.

1. CRUD completo de categoria.
2. Preservação de acentuação UTF-8 na carga inicial.
3. CRUD de ocorrência e atualização, incluindo vínculo e restrição de exclusão.
4. Atualização pela rota principal e bloqueio de transferência para outro protocolo.
5. Duplicidade ativa ignorando caixa e espaços externos.
6. Fluxo de status sem saltos, histórico automático e novo registro após resolução.
7. Status inicial ABERTA e referências existentes.
8. JSON padronizado para 400, 404 e 405, incluindo JSON malformado e parâmetros inválidos.
9. Restrições de integridade e ausência de SQL/trace na resposta.
10. PreparedStatement tratando uma string semelhante a SQL como dado literal.
11. CORS permitindo a origem configurada e recusando outra origem.

## Evidências

`docs/evidencias/resultado-browser.json` registra 13 verificações do navegador. As capturas incluem Home em três resoluções, formulário inválido, sucesso do cadastro, detalhes com histórico, estado vazio e falha de rede. Dados usados nas imagens são **sintéticos**, criados por chamadas à API Java local.

Na inspeção visual foi identificado texto inicial com acentuação incorreta no Windows. Foi configurado `spring.sql.init.encoding=UTF-8`, corrigida a carga do banco local de demonstração e adicionado teste de regressão. As imagens finais foram atualizadas após a correção.

## Ambiente e limites

- Windows, Java 17.0.12, Node.js 24.16.0, npm 12.2.1, Maven Wrapper 3.9.11, Spring Boot 3.5.7, Vite 7.3.8.
- Navegador de verificação: Microsoft Edge em modo headless, via Playwright.
- API local verificada em `http://localhost:8081`, pois a porta 8080 já estava ocupada por outro serviço. CORS foi exercitado a partir de `http://127.0.0.1:5173`.
- Persistência testada: H2 em memória nos testes Java e H2 em arquivo no fluxo do navegador.
- **Não executado:** conexão Oracle FIAP, modelos nativos do Data Modeler, execução de imagem Docker, publicação no Render/Vercel, vídeos, leitor de tela e ensaio de carga/concorrência.
- A regra de duplicidade é aplicada no serviço. Concorrência entre gravações simultâneas exige reforço antes de operação pública.
- O fluxo de gestão é uma demonstração acadêmica aberta; autenticação e autorização são requisitos de evolução.

Os testes locais não comprovam disponibilidade remota nem execução de serviços pela prefeitura.

## Atualização de identificação e Engenharia — 13/11/2026

- Build TypeScript/Vite aprovado após atualizar a página Integrantes e o contato da equipe.
- Verificados os cinco nomes e os cinco links de e-mail institucionais na interface em 390×844, 768×1024 e 1440×900, sem overflow horizontal.
- Confirmado Arthur Mendes Garcia como representante e contato principal.
- Quadro HTML local: 13 histórias; busca e expansão de RF/RN/RNF verificadas.
- PDFs: identificação dos cinco integrantes validada por leitura do texto; nenhuma imagem quebrada ou sobreposição de rodapé detectada no HTML usado na impressão.
- Engenharia: 37 páginas com backlog detalhado e seis telas/estados incorporados; ROI acadêmico conferido em 25% para as premissas do documento.
- Arquivo de resultados: `docs/evidencias/validacao-atualizacao-grupo.json`.
- Java não sofreu alteração de código nesta atualização; os onze testes anteriores continuam sendo a evidência técnica do backend.
