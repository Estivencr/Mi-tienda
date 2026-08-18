package com.mitienda.tienda.pedido;

import com.mitienda.tienda.producto.Producto;
import com.mitienda.tienda.producto.ProductoRepository;
import com.mitienda.tienda.shared.AccesoDenegadoException;
import com.mitienda.tienda.shared.RecursoNoEncontradoException;
import com.mitienda.tienda.usuario.Usuario;
import com.mitienda.tienda.usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PedidoResponse crear(PedidoRequest request) {
        Usuario usuario = usuarioActual();

        Pedido pedido = new Pedido(usuario);

        for (LineaPedidoRequest lineaReq : request.lineas()) {
            Producto producto = productoRepository.findById(lineaReq.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe un producto con id " + lineaReq.productoId()));

            LineaPedido linea = new LineaPedido(producto, lineaReq.cantidad());
            pedido.agregarLinea(linea);
        }

        Pedido guardado = pedidoRepository.save(pedido);
        return toResponse(guardado);
    }

    public List<PedidoResponse> misPedidos() {
        String email = usuarioActual().getEmail();
        return pedidoRepository.findByUsuarioEmail(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un pedido con id " + id));

        verificarPropietario(pedido);
        return toResponse(pedido);
    }

    public PedidoResponse cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un pedido con id " + id));

        verificarPropietario(pedido);
        pedido.cancelar();

        Pedido guardado = pedidoRepository.save(pedido);
        return toResponse(guardado);
    }

    private Usuario usuarioActual() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario autenticado no encontrado: " + email));
    }

    private void verificarPropietario(Pedido pedido) {
        String emailActual = usuarioActual().getEmail();
        if (!pedido.getUsuario().getEmail().equals(emailActual)) {
            throw new AccesoDenegadoException("No tienes permiso sobre este pedido");
        }
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<LineaPedidoResponse> lineas = pedido.getLineas().stream()
                .map(l -> new LineaPedidoResponse(
                        l.getProducto().getNombre(),
                        l.getCantidad(),
                        l.getPrecioUnitario(),
                        l.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getEmail(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getTotal(),
                lineas
        );
    }
}