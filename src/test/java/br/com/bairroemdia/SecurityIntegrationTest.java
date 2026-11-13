package br.com.bairroemdia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:seguranca;MODE=Oracle;DB_CLOSE_DELAY=-1", "app.admin.token=" + ApiIntegrationTest.ADMIN_TOKEN})
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    private String auth() { return "Bearer " + ApiIntegrationTest.ADMIN_TOKEN; }
    private Map<String, Object> occurrence(String address, String status) {
        return Map.of("titulo", "Denúncia de teste", "descricao", "Problema de iluminação registrado por um morador.",
            "endereco", address, "bairro", "Vila Mariana", "categoriaId", 1, "prioridade", "ALTA", "status", status);
    }
    private JsonNode create(String address) throws Exception {
        return json.readTree(mvc.perform(post("/ocorrencias").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(occurrence(address, "ABERTA"))))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    @Test void visitanteNaoPodeExcluirDenunciaMesmoSemHistorico() throws Exception {
        long id = create("Rua da proteção, 10").get("id").asLong();
        mvc.perform(delete("/ocorrencias/" + id)).andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(401)).andExpect(jsonPath("$.campos").isMap());
        mvc.perform(get("/ocorrencias/" + id)).andExpect(status().isOk());
    }

    @Test void chaveErradaEPerfilForjadoNaoPermitemExclusao() throws Exception {
        long id = create("Rua da proteção, 20").get("id").asLong();
        mvc.perform(delete("/ocorrencias/" + id).header("Authorization", "Bearer chave-falsa"))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.mensagem", not(containsString(ApiIntegrationTest.ADMIN_TOKEN))));
        mvc.perform(delete("/ocorrencias/" + id).header("X-Role", "ADMIN").param("admin", "true"))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/ocorrencias/" + id)).andExpect(status().isOk());
    }

    @Test void visitanteNaoPodeEditarOuMudarSituacao() throws Exception {
        long id = create("Rua da proteção, 30").get("id").asLong();
        mvc.perform(put("/ocorrencias/" + id).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(occurrence("Rua alterada, 10", "EM_ANALISE")))).andExpect(status().isUnauthorized());
        mvc.perform(get("/ocorrencias/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ABERTA"))
            .andExpect(jsonPath("$.endereco").value("Rua da proteção, 30"));
    }

    @Test void visitantePodeComentarMasNaoApagarOuEditarHistorico() throws Exception {
        long id = create("Rua da proteção, 40").get("id").asLong();
        var response = mvc.perform(post("/ocorrencias/" + id + "/atualizacoes").contentType(MediaType.APPLICATION_JSON)
            .content("{\"autor\":\"Morador\",\"mensagem\":\"O problema continua no local.\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse();
        long updateId = json.readTree(response.getContentAsString()).get("id").asLong();
        mvc.perform(delete("/atualizacoes/" + updateId)).andExpect(status().isUnauthorized());
        mvc.perform(put("/atualizacoes/" + updateId).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/ocorrencias/" + id + "/atualizacoes")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void equipeAutenticadaPodeExcluirRegistroSemVinculos() throws Exception {
        long id = create("Rua da proteção, 50").get("id").asLong();
        mvc.perform(delete("/ocorrencias/" + id).header("Authorization", auth())).andExpect(status().isNoContent());
        mvc.perform(get("/ocorrencias/" + id)).andExpect(status().isNotFound());
    }

    @Test void equipePodeMudarStatusEPreservaRestricaoDeHistorico() throws Exception {
        long id = create("Rua da proteção, 60").get("id").asLong();
        mvc.perform(put("/ocorrencias/" + id).header("Authorization", auth()).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(occurrence("Rua da proteção, 60", "EM_ANALISE"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("EM_ANALISE"));
        mvc.perform(get("/ocorrencias/" + id + "/atualizacoes")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete("/ocorrencias/" + id).header("Authorization", auth())).andExpect(status().isConflict());
    }

    @Test void administracaoDeCategoriasTambemExigeEquipe() throws Exception {
        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(put("/categorias/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(delete("/categorias/1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
    }

    @Test void acessoDaEquipeExigeChaveENaoCriaCookieOuExpoeSegredo() throws Exception {
        mvc.perform(get("/gestao/sessao")).andExpect(status().isUnauthorized());
        mvc.perform(get("/gestao/sessao").header("Authorization", auth()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.perfil").value("ADMIN"))
            .andExpect(header().doesNotExist("Set-Cookie")).andExpect(content().string(not(containsString(ApiIntegrationTest.ADMIN_TOKEN))));
    }

    @Test void corsPermiteAuthorizationSomenteParaOrigensConfiguradas() throws Exception {
        mvc.perform(options("/ocorrencias/1").header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "DELETE").header("Access-Control-Request-Headers", "Authorization"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Headers", containsString("Authorization")));
        mvc.perform(options("/ocorrencias/1").header("Origin", "https://site-nao-autorizado.example")
            .header("Access-Control-Request-Method", "DELETE").header("Access-Control-Request-Headers", "Authorization"))
            .andExpect(status().isForbidden());
    }
}
