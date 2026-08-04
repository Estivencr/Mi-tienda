package com.mitienda.tienda;

import com.mitienda.tienda.usuario.Usuario;
import com.mitienda.tienda.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TiendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaApplication.class, args);
	}

	@Bean
	public CommandLineRunner cargarDatos(UsuarioRepository usuarioRepository,
                                         PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepository.findByEmail("admin@tienda.com").isEmpty()) {
				Usuario admin = new Usuario(
						"admin@tienda.com",
						passwordEncoder.encode("password123"),
						"ADMIN"
				);
				usuarioRepository.save(admin);
			}

			if (usuarioRepository.findByEmail("cliente@tienda.com").isEmpty()) {
				Usuario cliente = new Usuario(
						"cliente@tienda.com",
						passwordEncoder.encode("cliente123"),
						"USER"
				);
				usuarioRepository.save(cliente);
			}
		};
	}

}
