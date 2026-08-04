package com.mitienda.tienda.producto;

import com.mitienda.tienda.shared.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    ProductoService productoService;

    @Test
    void listar_todos_devuelve_lista_de_responses() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Teclado");
        p.setDescripcion("RGB");
        p.setPrecio(120.0);
        p.setStock(15);

        when(productoRepository.findAll()).thenReturn(List.of(p));

        List<ProductoResponse> resultado = productoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Teclado");
        assertThat(resultado.get(0).id()).isEqualTo(1L);
    }

    @Test
    void buscar_por_id_inexistente_lanza_excepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.buscarPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void buscar_por_id_existente_debuelve_response() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Teclado");
        p.setDescripcion("RGB");
        p.setPrecio(120.0);
        p.setStock(15);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductoResponse resultado = productoService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Teclado");
    }



    @Test
    void crear_producto_guarda_y_devuelve_response() {
        ProductoRequest request = new ProductoRequest(
                "Mouse", "Inalámbrico", 45.0, 10);

        Producto guardado = new Producto();
        guardado.setId(1L);
        guardado.setNombre("Mouse");
        guardado.setDescripcion("Inalámbrico");
        guardado.setPrecio(45.0);
        guardado.setStock(10);

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponse response = productoService.crear(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nombre()).isEqualTo("Mouse");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void eliminar_producto_existente_llama_deleteById() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminar_producto_inexistente_lanza_exepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void actualizar_producto_existente_devuelve_response_actualizado(){
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setNombre("Teclado viejo");
        existente.setDescripcion("un teclado que tiene teclas");
        existente.setPrecio(100.0);
        existente.setStock(5);

        ProductoRequest request = new ProductoRequest("Teclado nuevo", "RGB", 150.0, 20);

        Producto actualizado = new Producto();
        actualizado.setId(1L);
        actualizado.setNombre("Teclado nuevo");
        actualizado.setDescripcion("RGB");
        actualizado.setPrecio(150.0);
        actualizado.setStock(20);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        ProductoResponse resultado = productoService.actualizar(1L, request);

        assertThat(resultado.nombre()).isEqualTo("Teclado nuevo");
        assertThat(resultado.precio()).isEqualTo(150.0);
    }

    @Test
    void actualizar_producto_inexistente_lanza_excepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        ProductoRequest request = new ProductoRequest("nom", "desq", 10.0, 1);

        assertThatThrownBy(() -> productoService.actualizar(99L, request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

}