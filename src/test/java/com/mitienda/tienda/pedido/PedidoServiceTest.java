package com.mitienda.tienda.pedido;

import com.mitienda.tienda.producto.Producto;
import com.mitienda.tienda.producto.ProductoRepository;
import com.mitienda.tienda.shared.AccesoDenegadoException;
import com.mitienda.tienda.shared.RecursoNoEncontradoException;
import com.mitienda.tienda.usuario.Usuario;
import com.mitienda.tienda.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    PedidoRepository pedidoRepository;

    @Mock
    ProductoRepository productoRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    PedidoService pedidoService;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    // ── Helper: simula un usuario autenticado en el contexto de Spring Security
    private void autenticarComo(String email) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ── Helper: crea un Usuario de prueba
    private Usuario usuario(String email) {
        Usuario u = new Usuario(email, "hash", "USER");
        u.setId(1L);
        return u;
    }

    // ── Helper: crea un Producto de prueba
    private Producto producto(Long id, String nombre, double precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(100);
        return p;
    }

    @Test
    void crear_pedido_con_una_linea_devuelve_response_correcto() {
        // Given
        String email = "cliente@tienda.com";
        autenticarComo(email);

        Usuario usuario = usuario(email);
        Producto teclado = producto(1L, "Teclado", 120.0);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(teclado));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PedidoRequest request = new PedidoRequest(
                List.of(new LineaPedidoRequest(1L, 2))
        );

        // When
        PedidoResponse response = pedidoService.crear(request);

        // Then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.usuarioEmail()).isEqualTo(email);
        assertThat(response.estado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(response.lineas()).hasSize(1);
        assertThat(response.lineas().get(0).nombreProducto()).isEqualTo("Teclado");
        assertThat(response.total()).isEqualByComparingTo("240.0");
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crear_pedido_con_producto_inexistente_lanza_excepcion() {
        // Given
        String email = "cliente@tienda.com";
        autenticarComo(email);

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario(email)));
        when(productoRepository.findById(99L))
                .thenReturn(Optional.empty());

        PedidoRequest request = new PedidoRequest(
                List.of(new LineaPedidoRequest(99L, 1))
        );

        // When / Then
        assertThatThrownBy(() -> pedidoService.crear(request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void cancelar_pedido_propio_devuelve_response_cancelado() {
        // Given
        String email = "cliente@tienda.com";
        autenticarComo(email);

        Usuario usuario = usuario(email);
        Pedido pedido = new Pedido(usuario);
        pedido.setId(1L);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // When
        PedidoResponse response = pedidoService.cancelar(1L);

        // Then
        assertThat(response.estado()).isEqualTo(EstadoPedido.CANCELADO);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void cancelar_pedido_ajeno_lanza_AccesoDenegadoException() {
        // Given
        autenticarComo("intruso@tienda.com");

        Usuario dueno = usuario("cliente@tienda.com");
        Usuario intruso = new Usuario("intruso@tienda.com", "hash", "USER");
        intruso.setId(2L);

        Pedido pedido = new Pedido(dueno);
        pedido.setId(1L);

        when(usuarioRepository.findByEmail("intruso@tienda.com"))
                .thenReturn(Optional.of(intruso));
        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        // When / Then
        assertThatThrownBy(() -> pedidoService.cancelar(1L))
                .isInstanceOf(AccesoDenegadoException.class)
                .hasMessageContaining("permiso");
    }

    @Test
    void mis_pedidos_devuelve_solo_los_del_usuario_autenticado() {
        // Given
        String email = "cliente@tienda.com";
        autenticarComo(email);

        Usuario usuario = usuario(email);
        Pedido pedido = new Pedido(usuario);
        pedido.setId(1L);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findByUsuarioEmail(email)).thenReturn(List.of(pedido));

        // When
        List<PedidoResponse> resultado = pedidoService.misPedidos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).usuarioEmail()).isEqualTo(email);
    }

    @Test
    void buscar_pedido_inexistente_lanza_excepcion() {
        // Given
        autenticarComo("cliente@tienda.com");

        when(pedidoRepository.findById(99L))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> pedidoService.buscarPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }
}
