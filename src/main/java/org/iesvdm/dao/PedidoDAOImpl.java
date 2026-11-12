package org.iesvdm.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {
    private final JdbcTemplate jdbcTemplate;

    public PedidoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Pedido pedido) {
        String sql = "INSERT INTO pedido (total, fecha, id_cliente, id_comercial) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            ps.setObject(1, pedido.getTotal(), Types.DOUBLE);
            if (pedido.getFecha() == null) ps.setNull(2, Types.DATE);
            else ps.setDate(2, Date.valueOf(pedido.getFecha()));
            ps.setInt(3, pedido.getClienteId());
            ps.setInt(4, pedido.getComercialId());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) pedido.setId(keyHolder.getKey().intValue());
        log.info("Insertados {} registros de Pedido.", rows);
    }

    @Override
    public List<PedidoDetalle> getAllDetalles() {
        return jdbcTemplate.query(sqlDetalle() + " ORDER BY p.id",
                (rs, rowNum) -> mapDetalle(rs));
    }

    @Override
    public List<PedidoDetalle> findByComercialId(int comercialId) {
        return jdbcTemplate.query(sqlDetalle() + " WHERE p.id_comercial = ? ORDER BY p.id",
                (rs, rowNum) -> mapDetalle(rs), comercialId);
    }

    @Override
    public List<PedidoDetalle> findByClienteId(int clienteId) {
        return jdbcTemplate.query(sqlDetalle() + " WHERE p.id_cliente = ? ORDER BY p.id",
                (rs, rowNum) -> mapDetalle(rs), clienteId);
    }

    @Override
    public Optional<Pedido> find(int id) {
        List<Pedido> pedidos = jdbcTemplate.query(
                "SELECT id, total, fecha, id_cliente, id_comercial FROM pedido WHERE id = ?",
                (rs, rowNum) -> new Pedido(rs.getInt("id"), rs.getObject("total", Double.class),
                        rs.getDate("fecha") == null ? null : rs.getDate("fecha").toLocalDate(),
                        rs.getInt("id_cliente"), rs.getInt("id_comercial")), id);
        return pedidos.stream().findFirst();
    }

    @Override
    public Optional<PedidoDetalle> findDetalle(int id) {
        List<PedidoDetalle> pedidos = jdbcTemplate.query(sqlDetalle() + " WHERE p.id = ?",
                (rs, rowNum) -> mapDetalle(rs), id);
        return pedidos.stream().findFirst();
    }

    @Override
    public void update(Pedido pedido) {
        int rows = jdbcTemplate.update("UPDATE pedido SET total = ?, fecha = ?, id_cliente = ?, id_comercial = ? WHERE id = ?",
                pedido.getTotal(), pedido.getFecha() == null ? null : Date.valueOf(pedido.getFecha()),
                pedido.getClienteId(), pedido.getComercialId(), pedido.getId());
        log.info("Update de Pedido con {} registros actualizados.", rows);
    }

    @Override
    public void delete(int id) {
        int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
        log.info("Delete de Pedido con {} registros eliminados.", rows);
    }

    private String sqlDetalle() {
        return """
                SELECT p.id, p.total, p.fecha, p.id_cliente, p.id_comercial,
                       CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS cliente_nombre,
                       CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS comercial_nombre
                FROM pedido p
                JOIN cliente c ON p.id_cliente = c.id
                JOIN comercial co ON p.id_comercial = co.id
                """;
    }

    private PedidoDetalle mapDetalle(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new PedidoDetalle(rs.getInt("id"), rs.getObject("total", Double.class),
                rs.getDate("fecha") == null ? null : rs.getDate("fecha").toLocalDate(),
                rs.getInt("id_cliente"), rs.getInt("id_comercial"),
                rs.getString("cliente_nombre"), rs.getString("comercial_nombre"));
    }
}
