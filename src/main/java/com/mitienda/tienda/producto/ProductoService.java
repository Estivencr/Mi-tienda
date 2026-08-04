package com.mitienda.tienda.producto;

import org.springframework.stereotype.Service;

import java.util.List;
import com.mitienda.tienda.shared.RecursoNoEncontradoException;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado);
    }

    public ProductoResponse buscarPorId(Long id){
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un producto con id " + id));
        return toResponse(producto);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un producto con id " + id));

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        Producto actualizado = productoRepository.save(producto);
        return toResponse(actualizado);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)){
            throw new RecursoNoEncontradoException("No existe un producto con id " + id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoResponse toResponse(Producto producto){
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}
