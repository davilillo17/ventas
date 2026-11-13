package org.iesvdm.dao;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.dto.ClienteComercialDTO;
import org.iesvdm.modelo.Comercial;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteComercialDAOImpl implements ClienteComercialDAO {

    private final JdbcTemplate jdbcTemplate;

    public ClienteComercialDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comercial> findDisponiblesByClienteId(int clienteId) {
        return jdbcTemplate.query("""
                SELECT c.id, c.nombre, c.apellido1, c.apellido2, c.comisión
                FROM comercial c
                WHERE NOT EXISTS (
                    SELECT 1 FROM cliente_has_comercial cc
                    WHERE cc.cliente_id = ? AND cc.comercial_id = c.id
                )
                ORDER BY c.apellido1, c.nombre
                """,
                (rs, rowNum) -> new Comercial(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getObject("comisión", Float.class)),
                clienteId);
    }

    @Override
    public List<ClienteComercialDTO> findVinculadosByClienteId(int clienteId) {
        return jdbcTemplate.query("""
                SELECT c.id, c.nombre, c.apellido1, c.apellido2, c.comisión,
                       cc.fecha_asociacion, cc.prioridad
                FROM cliente_has_comercial cc
                JOIN comercial c ON c.id = cc.comercial_id
                WHERE cc.cliente_id = ?
                ORDER BY cc.prioridad, c.apellido1, c.nombre
                """,
                (rs, rowNum) -> new ClienteComercialDTO(
                        new Comercial(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getString("apellido1"),
                                rs.getString("apellido2"),
                                rs.getObject("comisión", Float.class)),
                        rs.getDate("fecha_asociacion").toLocalDate(),
                        rs.getInt("prioridad")),
                clienteId);
    }

    @Override
    public void vincular(int clienteId, int comercialId, LocalDate fechaAsociacion, int prioridad) {
        jdbcTemplate.update("""
                INSERT INTO cliente_has_comercial
                    (cliente_id, comercial_id, fecha_asociacion, prioridad)
                VALUES (?, ?, ?, ?)
                """, clienteId, comercialId, fechaAsociacion, prioridad);
    }

    @Override
    public void desvincular(int clienteId, int comercialId) {
        jdbcTemplate.update("""
                DELETE FROM cliente_has_comercial
                WHERE cliente_id = ? AND comercial_id = ?
                """, clienteId, comercialId);
    }
}