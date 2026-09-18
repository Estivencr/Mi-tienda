package com.mitienda.tienda.pedido;

import com.mitienda.tienda.usuario.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();

    protected Pedido() { }

    public Pedido(Usuario usuario) {
        this.usuario = usuario;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.total = BigDecimal.ZERO;
    }

    public void agregarLinea(LineaPedido linea) {
        linea.setPedido(this);
        this.lineas.add(linea);
        recalcularTotal();
    }

    public void cancelar() {
        if (this.estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se puede cancelar un pedido pendiente");
        }
        this.estado = EstadoPedido.CANCELADO;
    }

    private void recalcularTotal() {
        this.total = lineas.stream()
                .map(LineaPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }
}