package br.com.bairroemdia.dao;
import br.com.bairroemdia.model.Ocorrencia;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;
@Repository
public class OcorrenciaDao extends JdbcDao {
    public OcorrenciaDao(DataSource dataSource) { super(dataSource); }
    private Ocorrencia map(ResultSet rs) throws SQLException {
        Ocorrencia m = new Ocorrencia();
        m.setId(rs.getLong("cd_ocorrencia"));
        m.setTitulo(rs.getString("nm_titulo"));
        m.setDescricao(rs.getString("ds_descricao"));
        m.setEndereco(rs.getString("ds_endereco"));
        m.setBairro(rs.getString("nm_bairro"));
        m.setCategoriaId(rs.getLong("cd_categoria"));
        m.setPrioridade(rs.getString("ds_prioridade"));
        m.setStatus(rs.getString("ds_status"));
        m.setCriadoEm(rs.getTimestamp("dt_criacao").toLocalDateTime());
        return m;
    }
    public List<Ocorrencia> listar() { return query("SELECT * FROM tb_ocorrencia ORDER BY cd_ocorrencia DESC", this::map); }
    public Optional<Ocorrencia> buscar(Long id) { return query("SELECT * FROM tb_ocorrencia WHERE cd_ocorrencia = ?", this::map, id).stream().findFirst(); }
    public Ocorrencia inserir(Ocorrencia m) {
        Long id = query("SELECT sq_tb_ocorrencia.NEXTVAL FROM dual", rs -> rs.getLong(1)).get(0);
        update("INSERT INTO tb_ocorrencia (cd_ocorrencia, nm_titulo, ds_descricao, ds_endereco, nm_bairro, cd_categoria, ds_prioridade, ds_status, dt_criacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", id, m.getTitulo(), m.getDescricao(), m.getEndereco(), m.getBairro(), m.getCategoriaId(), m.getPrioridade(), m.getStatus(), m.getCriadoEm());
        m.setId(id);
        return m;
    }
    public Ocorrencia atualizar(Ocorrencia m) {
        update("UPDATE tb_ocorrencia SET nm_titulo = ?, ds_descricao = ?, ds_endereco = ?, nm_bairro = ?, cd_categoria = ?, ds_prioridade = ?, ds_status = ?, dt_criacao = ? WHERE cd_ocorrencia = ?", m.getTitulo(), m.getDescricao(), m.getEndereco(), m.getBairro(), m.getCategoriaId(), m.getPrioridade(), m.getStatus(), m.getCriadoEm(), m.getId());
        return m;
    }
    public void remover(Long id) { update("DELETE FROM tb_ocorrencia WHERE cd_ocorrencia = ?", id); }

}
