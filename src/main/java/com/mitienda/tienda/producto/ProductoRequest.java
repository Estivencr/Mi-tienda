package com.mitienda.tienda.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @Positive(message= "El precio debe ser mayor que cero")
        double precio,

        @PositiveOrZero(message = "El stock no puede ser negativo")
        int stock
) {
        public ProductoRequest {
                if (nombre != null){
                        nombre = nombre.trim();
                }
                if (descripcion != null){
                        descripcion = descripcion.trim();
                }
        }
}