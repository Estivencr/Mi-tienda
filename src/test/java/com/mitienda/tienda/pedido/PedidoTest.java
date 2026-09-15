package com.mitienda.tienda.pedido;

import com.mitienda.tienda.producto.Producto;
import com.mitienda.tienda.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoTest {

    private Producto producto(String nombre, double precio) {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(100);
        return p;
    }

    @Test
    void crearPedidoPendiente() {
        Usuario usuario = new Usuario("cliente@tienda.com", "hash", "USER");

        Pedido pedido = new Pedido(usuario);

        assertThat(pedido.getUsuario()).isEqualTo(usuario);
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(pedido.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(pedido.getFecha()).isNotNull();
        assertThat(pedido.getLineas()).isEmpty();
    }

    @Test
    void agregarLinea_asocia_el_pedido_y_recalcula_el_total() {
        Pedido pedido = new Pedido(new Usuario("cliente@tienda.com", "hash", "USER"));
        LineaPedido linea = new LineaPedido(producto("Teclado", 100.0), 2);

        pedido.agregarLinea(linea);

        assertThat(pedido.getLineas()).containsExactly(linea);
        assertThat(linea.getPedido()).isEqualTo(pedido);
        assertThat(pedido.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(200.0));
    }

    @Test
    void agregarLinea_con_varias_lineas_suma_los_subtotales() {
        Pedido pedido = new Pedido(new Usuario("cliente@tienda.com", "hash", "USER"));

        pedido.agregarLinea(new LineaPedido(producto("Teclado", 100.0), 2));
        pedido.agregarLinea(new LineaPedido(producto("Mouse", 45.0), 1));

        assertThat(pedido.getLineas()).hasSize(2);
        assertThat(pedido.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(245.0));
    }

    @Test
    void cancelar_pedido_pendiente_cambia_el_estado_a_cancelado() {
        Pedido pedido = new Pedido(new Usuario("cliente@tienda.com", "hash", "USER"));

        pedido.cancelar();

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
    }

    @Test
    void cancelar_pedido_ya_cancelado_lanza_excepcion() {
        Pedido pedido = new Pedido(new Usuario("cliente@tienda.com", "hash", "USER"));
        pedido.cancelar();

        assertThatThrownBy(pedido::cancelar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pendiente");
    }
}
