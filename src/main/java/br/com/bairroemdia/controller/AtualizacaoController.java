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
@RestController @Validated @RequestMapping("/atualizacoes")
public class AtualizacaoController {
    private final ZeladoriaService service;
    public AtualizacaoController(ZeladoriaService service) { this.service=service; }
    @GetMapping public List<AtualizacaoSaida> listar() { return service.listarAtualizacoes(); }
    @GetMapping("/{id}") public AtualizacaoSaida buscar(@PathVariable @Positive Long id) { return service.atualizacao(id); }
    @PostMapping public ResponseEntity<AtualizacaoSaida> criar(@Valid @RequestBody AtualizacaoEntrada dto) {
        var salvo=service.salvarAtualizacao(null,dto);
        return ResponseEntity.created(URI.create("/atualizacoes/"+salvo.getId())).body(salvo);
    }
    @PutMapping("/{id}") public AtualizacaoSaida atualizar(@PathVariable @Positive Long id,@Valid @RequestBody AtualizacaoEntrada dto) { return service.salvarAtualizacao(id,dto); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable @Positive Long id) { service.removerAtualizacao(id); return ResponseEntity.noContent().build(); }

}
