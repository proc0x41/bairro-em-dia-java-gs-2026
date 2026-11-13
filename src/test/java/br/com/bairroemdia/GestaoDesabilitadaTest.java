package br.com.bairroemdia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:semgestao;MODE=Oracle;DB_CLOSE_DELAY=-1", "app.admin.token="})
@ActiveProfiles("local")
@AutoConfigureMockMvc
class GestaoDesabilitadaTest {
    @Autowired MockMvc mvc;
    @Test void semChaveConfiguradaNenhumaChaveAutorizaGestao() throws Exception {
        mvc.perform(get("/gestao/sessao").header("Authorization", "Bearer " + ApiIntegrationTest.ADMIN_TOKEN))
            .andExpect(status().isUnauthorized());
        mvc.perform(delete("/ocorrencias/1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/categorias")).andExpect(status().isOk());
    }
}
