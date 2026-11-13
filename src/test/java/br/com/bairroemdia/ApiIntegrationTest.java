package br.com.bairroemdia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Testa HTTP -> validação -> serviço -> DAO -> banco H2, sem mock de persistência. */
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:teste;MODE=Oracle;DB_CLOSE_DELAY=-1", "app.admin.token=" + ApiIntegrationTest.ADMIN_TOKEN})
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest {
    static final String ADMIN_TOKEN = "chave-exclusiva-dos-testes-de-integracao-0001";
    // These tests exercise business flows as the team; SecurityIntegrationTest covers public access.
    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder post(String path, Object... args) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(path, args).header("Authorization", "Bearer " + ADMIN_TOKEN);
    }
    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder put(String path, Object... args) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(path, args).header("Authorization", "Bearer " + ADMIN_TOKEN);
    }
    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder delete(String path, Object... args) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(path, args).header("Authorization", "Bearer " + ADMIN_TOKEN);
    }
    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder patch(String path, Object... args) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(path, args).header("Authorization", "Bearer " + ADMIN_TOKEN);
    }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private Map<String,Object> ocorrencia(String endereco, String status) {
        return Map.of("titulo","Buraco em frente à escola", "descricao","Buraco grande dificulta a travessia dos moradores.","endereco",endereco,"bairro","Vila Mariana","categoriaId",1,"prioridade","ALTA","status",status);
    }
    private JsonNode criar(String rota, Object body) throws Exception {
        var result=mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)))
            .andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }
    @Test void crudCompletoCategoria() throws Exception {
        long id=criar("/categorias",Map.of("nome","Drenagem","descricao","Bueiros obstruídos e alagamentos.","prazoDias",10)).get("id").asLong();
        mvc.perform(get("/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id",hasItem((int)id)));
        mvc.perform(get("/categorias/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Drenagem"));
        mvc.perform(put("/categorias/"+id).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("nome","Drenagem urbana","descricao","Bueiros e pontos de alagamento.","prazoDias",8))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.prazoDias").value(8));
        mvc.perform(delete("/categorias/"+id)).andExpect(status().isNoContent());
        mvc.perform(get("/categorias/"+id)).andExpect(status().isNotFound());
    }
    @Test void cargaInicialPreservaAcentuacaoUtf8() throws Exception {
        mvc.perform(get("/categorias/2")).andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Iluminação pública"));
    }
    @Test void crudCompletoOcorrenciaEAtualizacao() throws Exception {
        long id=criar("/ocorrencias",ocorrencia("Rua do teste, 100","ABERTA")).get("id").asLong();
        mvc.perform(get("/ocorrencias")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id",hasItem((int)id)));
        mvc.perform(get("/ocorrencias/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.categoriaId").value(1));
        mvc.perform(put("/ocorrencias/"+id).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(ocorrencia("Rua do teste, 102","ABERTA"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.endereco").value("Rua do teste, 102"));
        long aid=criar("/ocorrencias/"+id+"/atualizacoes",Map.of("autor","Morador","mensagem","O problema continua no local.")).get("id").asLong();
        mvc.perform(get("/atualizacoes")).andExpect(status().isOk()).andExpect(jsonPath("$[*].id",hasItem((int)aid)));
        mvc.perform(get("/atualizacoes/"+aid)).andExpect(status().isOk());
        mvc.perform(get("/ocorrencias/"+id+"/atualizacoes")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/atualizacoes/"+aid).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("ocorrenciaId",id,"autor","Morador","mensagem","Equipe identificou o problema no local."))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.mensagem").value("Equipe identificou o problema no local."));
        mvc.perform(delete("/ocorrencias/"+id)).andExpect(status().isConflict());
        mvc.perform(delete("/atualizacoes/"+aid)).andExpect(status().isNoContent());
        mvc.perform(delete("/ocorrencias/"+id)).andExpect(status().isNoContent());
        mvc.perform(get("/ocorrencias/"+id)).andExpect(status().isNotFound());
    }
    @Test void atualizacaoPelaRotaPrincipalEPreservacaoDoVinculo() throws Exception {
        long id=criar("/ocorrencias",ocorrencia("Rua das flores, 55","ABERTA")).get("id").asLong();
        long outro=criar("/ocorrencias",ocorrencia("Rua das flores, 56","ABERTA")).get("id").asLong();
        long aid=criar("/atualizacoes",Map.of("ocorrenciaId",id,"autor","Equipe","mensagem","Vistoria solicitada.")).get("id").asLong();
        mvc.perform(put("/atualizacoes/"+aid).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("ocorrenciaId",outro,"autor","Equipe","mensagem","Transferir histórico."))))
            .andExpect(status().isConflict());
    }
    @Test void duplicidadeAtivaIgnoraCaixaEEspacosExternos() throws Exception {
        criar("/ocorrencias",ocorrencia("Rua das Águias, 12","ABERTA"));
        mvc.perform(post("/ocorrencias").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(ocorrencia("  RUA DAS ÁGUIAS, 12  ","ABERTA"))))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.mensagem",containsString("Já existe")));
    }
    @Test void fluxoDeStatusNaoPodePularEtapasERegistraHistorico() throws Exception {
        long id=criar("/ocorrencias",ocorrencia("Rua Horizonte, 90","ABERTA")).get("id").asLong();
        mvc.perform(put("/ocorrencias/"+id).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(ocorrencia("Rua Horizonte, 90","RESOLVIDA")))).andExpect(status().isConflict());
        for(String etapa:new String[]{"EM_ANALISE","EM_ANDAMENTO","RESOLVIDA"})
            mvc.perform(put("/ocorrencias/"+id).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(ocorrencia("Rua Horizonte, 90",etapa)))).andExpect(status().isOk());
        mvc.perform(get("/ocorrencias/"+id+"/atualizacoes")).andExpect(jsonPath("$.length()").value(3));
        mvc.perform(put("/ocorrencias/"+id).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(ocorrencia("Rua Horizonte, 90","ABERTA")))).andExpect(status().isConflict());
        criar("/ocorrencias",ocorrencia("Rua Horizonte, 90","ABERTA"));
    }
    @Test void cadastroComecaAbertoEReferenciasDevemExistir() throws Exception {
        mvc.perform(post("/ocorrencias").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(ocorrencia("Rua Nova, 15","EM_ANALISE")))).andExpect(status().isConflict());
        mvc.perform(post("/atualizacoes").contentType(MediaType.APPLICATION_JSON).content("{\"ocorrenciaId\":99999,\"autor\":\"Equipe\",\"mensagem\":\"Registro inexistente.\"}"))
            .andExpect(status().isNotFound());
        mvc.perform(post("/ocorrencias/99999/atualizacoes").contentType(MediaType.APPLICATION_JSON).content("{\"autor\":\"Equipe\",\"mensagem\":\"Registro inexistente.\"}"))
            .andExpect(status().isNotFound());
    }
    @Test void validacao400Ausente404EMetodo405SaoJsonPadronizado() throws Exception {
        mvc.perform(post("/ocorrencias").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos.titulo").exists()).andExpect(jsonPath("$.timestamp").exists());
        mvc.perform(get("/ocorrencias/99999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.caminho").value("/ocorrencias/99999"));
        mvc.perform(get("/ocorrencias/abc")).andExpect(status().isBadRequest());
        mvc.perform(get("/ocorrencias/-1")).andExpect(status().isBadRequest());
        mvc.perform(patch("/categorias/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isMethodNotAllowed()).andExpect(header().exists("Allow")).andExpect(jsonPath("$.status").value(405));
        mvc.perform(get("/rota-inexistente")).andExpect(status().isNotFound()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content("{invalido"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.trace").doesNotExist());
    }
    @Test void vinculosEDuplicidadeNaoExpoemSql() throws Exception {
        criar("/ocorrencias",ocorrencia("Rua das Acácias, 30","ABERTA"));
        mvc.perform(delete("/categorias/1")).andExpect(status().isConflict()).andExpect(jsonPath("$.mensagem",not(containsString("SQL"))));
        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Buraco na via\",\"descricao\":\"Descrição duplicada de categoria.\",\"prazoDias\":5}"))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.trace").doesNotExist());
    }
    @Test void preparedStatementTrataTextoComoDado() throws Exception {
        String texto="Rua '; DROP TABLE tb_categoria; --, 80";
        long id=criar("/ocorrencias",ocorrencia(texto,"ABERTA")).get("id").asLong();
        mvc.perform(get("/ocorrencias/"+id)).andExpect(jsonPath("$.endereco").value(texto));
        mvc.perform(get("/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
    }
    @Test void corsPermiteOrigemConfiguradaEBloqueiaOutra() throws Exception {
        mvc.perform(options("/ocorrencias").header("Origin","http://localhost:5173").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","Content-Type"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:5173"));
        mvc.perform(options("/ocorrencias").header("Origin","https://origem-nao-autorizada.example").header("Access-Control-Request-Method","POST"))
            .andExpect(status().isForbidden());
    }
}
