package br.com.bairroemdia.dto;
import jakarta.validation.constraints.*;
public class CategoriaEntrada {
    @NotBlank @Size(max = 80) private String nome;
    @NotBlank @Size(max = 500) private String descricao;
    @NotNull @Min(1) @Max(90) private Integer prazoDias;
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getPrazoDias() { return prazoDias; }
    public void setPrazoDias(Integer prazoDias) { this.prazoDias = prazoDias; }
}
