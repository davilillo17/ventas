package org.iesvdm.dto;

import java.time.LocalDate;

import org.iesvdm.modelo.Comercial;

public class ClienteComercialDTO {
    private final Comercial comercial;
    private final LocalDate fechaAsociacion;
    private final int prioridad;

    public ClienteComercialDTO(Comercial comercial, LocalDate fechaAsociacion, int prioridad) {
        this.comercial = comercial;
        this.fechaAsociacion = fechaAsociacion;
        this.prioridad = prioridad;
    }

    public Comercial getComercial() {
        return comercial;
    }

    public LocalDate getFechaAsociacion() {
        return fechaAsociacion;
    }

    public int getPrioridad() {
        return prioridad;
    }
}