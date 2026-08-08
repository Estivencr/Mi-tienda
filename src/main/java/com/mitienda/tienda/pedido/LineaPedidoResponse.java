package com.mitienda.tienda.pedido;

import java.math.BigDecimal;

public record LineaPedidoResponse(
        String nombreProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}