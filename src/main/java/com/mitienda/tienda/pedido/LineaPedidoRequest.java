package com.mitienda.tienda.pedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LineaPedidoRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @Positive(message = "La cantidad debe ser mayor que cero")
        int cantidad
) {
}