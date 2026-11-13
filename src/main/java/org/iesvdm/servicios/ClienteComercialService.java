package org.iesvdm.servicios;

import java.time.LocalDate;
import java.util.List;

import org.iesvdm.dao.ClienteComercialDAO;
import org.iesvdm.dto.ClienteComercialDTO;
import org.iesvdm.modelo.Comercial;
import org.springframework.stereotype.Service;

@Service
public class ClienteComercialService {

    private final ClienteComercialDAO clienteComercialDAO;

    public ClienteComercialService(ClienteComercialDAO clienteComercialDAO) {
        this.clienteComercialDAO = clienteComercialDAO;
    }

    public List<Comercial> findDisponiblesByClienteId(int clienteId) {
        return clienteComercialDAO.findDisponiblesByClienteId(clienteId);
    }

    public List<ClienteComercialDTO> findVinculadosByClienteId(int clienteId) {
        return clienteComercialDAO.findVinculadosByClienteId(clienteId);
    }

    public void vincular(int clienteId, int comercialId, LocalDate fechaAsociacion, int prioridad) {
        clienteComercialDAO.vincular(clienteId, comercialId, fechaAsociacion, prioridad);
    }

    public void desvincular(int clienteId, int comercialId) {
        clienteComercialDAO.desvincular(clienteId, comercialId);
    }
}