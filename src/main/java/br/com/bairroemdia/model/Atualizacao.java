package br.com.bairroemdia.model;
public class Atualizacao {
    private Long id;
    private Long ocorrenciaId;
    private String mensagem;
    private String autor;
    private java.time.LocalDateTime criadoEm;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOcorrenciaId() { return ocorrenciaId; }
    public void setOcorrenciaId(Long ocorrenciaId) { this.ocorrenciaId = ocorrenciaId; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public java.time.LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(java.time.LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
