package br.com.bairroemdia.dao;
import br.com.bairroemdia.model.Categoria;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;
@Repository
public class CategoriaDao extends JdbcDao {
    public CategoriaDao(DataSource dataSource) { super(dataSource); }
    private Categoria map(ResultSet rs) throws SQLException {
        Categoria m = new Categoria();
        m.setId(rs.getLong("cd_categoria"));
        m.setNome(rs.getString("nm_categoria"));
        m.setDescricao(rs.getString("ds_categoria"));
        m.setPrazoDias(rs.getInt("nr_prazo_dias"));
        return m;
    }
    public List<Categoria> listar() { return query("SELECT * FROM tb_categoria ORDER BY cd_categoria DESC", this::map); }
    public Optional<Categoria> buscar(Long id) { return query("SELECT * FROM tb_categoria WHERE cd_categoria = ?", this::map, id).stream().findFirst(); }
    public Categoria inserir(Categoria m) {
        Long id = query("SELECT sq_tb_categoria.NEXTVAL FROM dual", rs -> rs.getLong(1)).get(0);
        update("INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, nr_prazo_dias) VALUES (?, ?, ?, ?)", id, m.getNome(), m.getDescricao(), m.getPrazoDias());
        m.setId(id);
        return m;
    }
    public Categoria atualizar(Categoria m) {
        update("UPDATE tb_categoria SET nm_categoria = ?, ds_categoria = ?, nr_prazo_dias = ? WHERE cd_categoria = ?", m.getNome(), m.getDescricao(), m.getPrazoDias(), m.getId());
        return m;
    }
    public void remover(Long id) { update("DELETE FROM tb_categoria WHERE cd_categoria = ?", id); }

}
