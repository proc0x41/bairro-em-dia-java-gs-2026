package br.com.bairroemdia.dao;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.dao.DataAccessResourceFailureException;
/** Base JDBC: valores sempre vinculados por PreparedStatement; conexões participam da transação Spring. */
public abstract class JdbcDao {
    private final DataSource dataSource;
    protected JdbcDao(DataSource dataSource) { this.dataSource = dataSource; }
    @FunctionalInterface protected interface Row<T> { T map(ResultSet rs) throws SQLException; }
    protected <T> List<T> query(String sql, Row<T> mapper, Object... args) {
        Connection c = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, args);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) result.add(mapper.map(rs));
                return result;
            }
        } catch (SQLException e) { throw translate(e); }
        finally { DataSourceUtils.releaseConnection(c, dataSource); }
    }
    protected void update(String sql, Object... args) {
        Connection c = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = c.prepareStatement(sql)) { bind(ps, args); ps.executeUpdate(); }
        catch (SQLException e) { throw translate(e); }
        finally { DataSourceUtils.releaseConnection(c, dataSource); }
    }
    private void bind(PreparedStatement ps, Object[] args) throws SQLException {
        for (int i=0; i<args.length; i++) {
            if (args[i] instanceof java.time.LocalDateTime dt) ps.setTimestamp(i+1, Timestamp.valueOf(dt));
            else ps.setObject(i+1, args[i]);
        }
    }
    private RuntimeException translate(SQLException e) {
        var translated = new SQLStateSQLExceptionTranslator().translate("persistencia", null, e);
        return translated != null ? translated : new DataAccessResourceFailureException("Falha de persistência", e);
    }
}
