package br.com.bairroemdia.dao;
import br.com.bairroemdia.model.Atualizacao;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;
@Repository
public class AtualizacaoDao extends JdbcDao {
    public AtualizacaoDao(DataSource dataSource) { super(dataSource); }
    private Atualizacao map(ResultSet rs) throws SQLException {
        Atualizacao m = new Atualizacao();
        m.setId(rs.getLong("cd_atualizacao"));
        m.setOcorrenciaId(rs.getLong("cd_ocorrencia"));
        m.setMensagem(rs.getString("ds_mensagem"));
        m.setAutor(rs.getString("nm_autor"));
        m.setCriadoEm(rs.getTimestamp("dt_criacao").toLocalDateTime());
        return m;
    }
    public List<Atualizacao> listar() { return query("SELECT * FROM tb_atualizacao ORDER BY cd_atualizacao DESC", this::map); }
    public Optional<Atualizacao> buscar(Long id) { return query("SELECT * FROM tb_atualizacao WHERE cd_atualizacao = ?", this::map, id).stream().findFirst(); }
    public Atualizacao inserir(Atualizacao m) {
        Long id = query("SELECT sq_tb_atualizacao.NEXTVAL FROM dual", rs -> rs.getLong(1)).get(0);
        update("INSERT INTO tb_atualizacao (cd_atualizacao, cd_ocorrencia, ds_mensagem, nm_autor, dt_criacao) VALUES (?, ?, ?, ?, ?)", id, m.getOcorrenciaId(), m.getMensagem(), m.getAutor(), m.getCriadoEm());
        m.setId(id);
        return m;
    }
    public Atualizacao atualizar(Atualizacao m) {
        update("UPDATE tb_atualizacao SET cd_ocorrencia = ?, ds_mensagem = ?, nm_autor = ?, dt_criacao = ? WHERE cd_atualizacao = ?", m.getOcorrenciaId(), m.getMensagem(), m.getAutor(), m.getCriadoEm(), m.getId());
        return m;
    }
    public void remover(Long id) { update("DELETE FROM tb_atualizacao WHERE cd_atualizacao = ?", id); }
    public List<Atualizacao> porOcorrencia(Long id) { return query("SELECT * FROM tb_atualizacao WHERE cd_ocorrencia = ? ORDER BY dt_criacao, cd_atualizacao", this::map, id); }
}
