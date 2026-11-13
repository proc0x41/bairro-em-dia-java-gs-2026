package br.com.bairroemdia.controller;
import br.com.bairroemdia.dto.*;
import br.com.bairroemdia.service.ZeladoriaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.List;
@RestController @Validated @RequestMapping("/ocorrencias")
public class OcorrenciaController {
    private final ZeladoriaService service;
    public OcorrenciaController(ZeladoriaService service) { this.service=service; }
    @GetMapping public List<OcorrenciaSaida> listar() { return service.listarOcorrencias(); }
    @GetMapping("/{id}") public OcorrenciaSaida buscar(@PathVariable @Positive Long id) { return service.ocorrencia(id); }
    @PostMapping public ResponseEntity<OcorrenciaSaida> criar(@Valid @RequestBody OcorrenciaEntrada dto) {
        var salvo=service.salvarOcorrencia(null,dto);
        return ResponseEntity.created(URI.create("/ocorrencias/"+salvo.getId())).body(salvo);
    }
    @PutMapping("/{id}") public OcorrenciaSaida atualizar(@PathVariable @Positive Long id,@Valid @RequestBody OcorrenciaEntrada dto) { return service.salvarOcorrencia(id,dto); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable @Positive Long id) { service.removerOcorrencia(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/{id}/atualizacoes") public List<AtualizacaoSaida> historico(@PathVariable @Positive Long id) { return service.porOcorrencia(id); }
    @PostMapping("/{id}/atualizacoes") public ResponseEntity<AtualizacaoSaida> comentar(@PathVariable @Positive Long id,@Valid @RequestBody ComentarioEntrada dto) {
        AtualizacaoEntrada entrada=new AtualizacaoEntrada(); entrada.setOcorrenciaId(id); entrada.setAutor(dto.autor()); entrada.setMensagem(dto.mensagem());
        var salvo=service.salvarAtualizacao(null,entrada);
        return ResponseEntity.created(URI.create("/atualizacoes/"+salvo.getId())).body(salvo);
    }
}
