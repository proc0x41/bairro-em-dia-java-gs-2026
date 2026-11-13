package br.com.bairroemdia.controller;

import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gestao")
public class GestaoController {
    @GetMapping("/sessao")
    public ResponseEntity<Map<String, String>> sessao() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("perfil", "ADMIN"));
    }
}
