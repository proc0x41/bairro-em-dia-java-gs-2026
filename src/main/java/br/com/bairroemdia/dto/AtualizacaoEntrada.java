package br.com.bairroemdia.dto;
import jakarta.validation.constraints.*;
public class AtualizacaoEntrada {
    @NotNull @Positive private Long ocorrenciaId;
    @NotBlank @Size(min = 5, max = 1000) private String mensagem;
    @NotBlank @Size(min = 2, max = 100) private String autor;
    public Long getOcorrenciaId() { return ocorrenciaId; }
    public void setOcorrenciaId(Long ocorrenciaId) { this.ocorrenciaId = ocorrenciaId; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
}
