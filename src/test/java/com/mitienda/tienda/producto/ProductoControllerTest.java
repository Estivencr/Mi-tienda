package com.mitienda.tienda.producto;


import com.mitienda.tienda.shared.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(com.mitienda.tienda.shared.ManejadorDeErrores.class)
class ProductoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductoService productoService;

    @Test
    void listar_devuelve_200_con_lista() throws Exception {
        ProductoResponse response = new ProductoResponse(1L, "Teclado", "RGB", 120.0, 15);
        when(productoService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Teclado"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void crear_con_datos_validos_devuelve_201() throws Exception {
        ProductoRequest request = new ProductoRequest("Mouse", "Inalámbrico", 45.0, 10);
        ProductoResponse response = new ProductoResponse(1L, "Mouse", "Inalámbrico", 45.0, 10);
        when(productoService.crear(any(ProductoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }

    @Test
    void crear_con_nombre_vacio_devuelve_400() throws Exception {
        ProductoRequest requestInvalido = new ProductoRequest("", "desc", 10.0, 5);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));
    }

    @Test
    void buscar_por_id_inexistente_devuelve_404() throws Exception {
        when(productoService.buscarPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe un producto con id 99"));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un producto con id 99"));
    }
    @Test
    void buscar_por_id_existente_devuelve_200() throws Exception {
        ProductoResponse response = new ProductoResponse(1L, "Teclado", "RGB", 120.0, 15);
        when(productoService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath(("$.nombre")).value("Teclado"));
    }

    @Test
    void eliminar_producto_existente_devuelve_204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify((productoService)).eliminar(1L);
    }

    @Test
    void eliminar_producto_inexistente_devuelve_404() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe un producto con id 99"))
                .when(productoService).eliminar(99L);

        mockMvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un producto con id 99"));
    }

    @Test
    void actualizar_producto_existente_devuelve_200() throws Exception {
        ProductoRequest request = new ProductoRequest("Teclado", "RGB", 150.0, 15);
        ProductoResponse response = new ProductoResponse(1L, "Teclado nuevo", "RGB", 150.0, 15);

        when(productoService.actualizar(eq(1L), any(ProductoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado nuevo"));

    }

    @Test
    void actualizar_producto_inexistente_devuelve_404() throws Exception {
        ProductoRequest request = new ProductoRequest("Teclado actualizado", "RGB", 180.0, 20);

        when(productoService.actualizar(eq(99L), any(ProductoRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("No existe un producto con id 99"));

        mockMvc.perform(put("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un producto con id 99"));


    }


}
