package org.iesvdm.dto;

public class ClienteTotalPedidosDTO {
	private final int id;
	private final String nombre;
	private final double totalPedidos;

	public ClienteTotalPedidosDTO(int id, String nombre, double totalPedidos) {
		this.id = id;
		this.nombre = nombre;
		this.totalPedidos = totalPedidos;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public double getTotalPedidos() {
		return totalPedidos;
	}
}