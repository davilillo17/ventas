package org.iesvdm.dao;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.dto.ClienteComercialDTO;
import org.iesvdm.modelo.Comercial;

public interface ClienteComercialDAO {
    List<Comercial> findDisponiblesByClienteId(int clienteId);
    List<ClienteComercialDTO> findVinculadosByClienteId(int clienteId);
    void vincular(int clienteId, int comercialId, LocalDate fechaAsociacion, int prioridad);
    void desvincular(int clienteId, int comercialId);
}