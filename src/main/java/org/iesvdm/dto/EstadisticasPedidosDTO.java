package org.iesvdm.dto;

import java.util.List;

import org.iesvdm.modelo.PedidoDetalle;

public class EstadisticasPedidosDTO {
	private final double total;
	private final double media;
	private final double maximo;
	private final double minimo;

	public EstadisticasPedidosDTO(List<PedidoDetalle> pedidos) {
		total = pedidos.stream().mapToDouble(PedidoDetalle::getTotal).sum();
		media = pedidos.isEmpty() ? 0 : total / pedidos.size();
		maximo = pedidos.stream().mapToDouble(PedidoDetalle::getTotal).max().orElse(0);
		minimo = pedidos.stream().mapToDouble(PedidoDetalle::getTotal).min().orElse(0);
	}

	public double getTotal() {
		return total;
	}

	public double getMedia() {
		return media;
	}

	public double getMaximo() {
		return maximo;
	}

	public double getMinimo() {
		return minimo;
	}
}