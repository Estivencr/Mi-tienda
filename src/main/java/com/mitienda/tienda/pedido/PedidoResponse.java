package com.mitienda.tienda.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String usuarioEmail,
        LocalDateTime fecha,
        EstadoPedido estado,
        BigDecimal total,
        List<LineaPedidoResponse> lineas
) {
}
