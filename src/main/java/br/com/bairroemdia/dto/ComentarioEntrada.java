package br.com.bairroemdia.dto;
import jakarta.validation.constraints.*;
public record ComentarioEntrada(@NotBlank @Size(min=2,max=100) String autor,@NotBlank @Size(min=5,max=1000) String mensagem) {}
