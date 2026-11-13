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
@RestController @Validated @RequestMapping("/categorias")
public class CategoriaController {
    private final ZeladoriaService service;
    public CategoriaController(ZeladoriaService service) { this.service=service; }
    @GetMapping public List<CategoriaSaida> listar() { return service.listarCategorias(); }
    @GetMapping("/{id}") public CategoriaSaida buscar(@PathVariable @Positive Long id) { return service.categoria(id); }
    @PostMapping public ResponseEntity<CategoriaSaida> criar(@Valid @RequestBody CategoriaEntrada dto) {
        var salvo=service.salvarCategoria(null,dto);
        return ResponseEntity.created(URI.create("/categorias/"+salvo.getId())).body(salvo);
    }
    @PutMapping("/{id}") public CategoriaSaida atualizar(@PathVariable @Positive Long id,@Valid @RequestBody CategoriaEntrada dto) { return service.salvarCategoria(id,dto); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable @Positive Long id) { service.removerCategoria(id); return ResponseEntity.noContent().build(); }

}
