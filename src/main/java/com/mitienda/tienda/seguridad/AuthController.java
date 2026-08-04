package com.mitienda.tienda.seguridad;

import com.mitienda.tienda.usuario.Usuario;
import com.mitienda.tienda.usuario.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtServicio jwtServicio;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;


    public AuthController(JwtServicio jwtServicio, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.jwtServicio = jwtServicio;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email());

        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(request.password(), usuarioOpt.get().getPassword())) {
            return ResponseEntity.status(401).body((Map.of("Error", "Credenciales inválidas")));
        }

        String token = jwtServicio.generarToken(request.email(), usuarioOpt.get().getRol());
        return ResponseEntity.ok(Map.of("token", token));
    }

}