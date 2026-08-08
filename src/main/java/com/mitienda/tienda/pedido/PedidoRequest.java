package com.mitienda.tienda.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoRequest(
        @NotEmpty(message = "El pedido debe tener al menos una línea")
        @Valid
        List<LineaPedidoRequest> lineas
) {
}
