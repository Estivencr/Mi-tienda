package com.mitienda.tienda.producto;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        double precio,
        int stock
) {
}
