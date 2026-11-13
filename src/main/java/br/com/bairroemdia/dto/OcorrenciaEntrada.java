package br.com.bairroemdia.dto;
import jakarta.validation.constraints.*;
public class OcorrenciaEntrada {
    @NotBlank @Size(min = 5, max = 120) private String titulo;
    @NotBlank @Size(min = 15, max = 2000) private String descricao;
    @NotBlank @Size(min = 5, max = 250) private String endereco;
    @NotBlank @Size(min = 2, max = 80) private String bairro;
    @NotNull @Positive private Long categoriaId;
    @NotBlank @Pattern(regexp = "BAIXA|MEDIA|ALTA") private String prioridade;
    @NotBlank @Pattern(regexp = "ABERTA|EM_ANALISE|EM_ANDAMENTO|RESOLVIDA") private String status;
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
