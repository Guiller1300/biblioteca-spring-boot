package com.arias.biblioteca_arias13.usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository repo;

    @GetMapping("/listar")
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario) {
        if (repo.existsByCorreo(usuario.getCorreo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un usuario con ese correo");
        }
        usuario.setId(null);
        Usuario guardado = repo.save(usuario);
        return ResponseEntity
                .created(URI.create("/api/usuarios/obtener/" + guardado.getId()))
                .body(guardado);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody Usuario datos) {
        Usuario actual = repo.findById(id).orElse(null);
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }
        if (!actual.getCorreo().equals(datos.getCorreo())
                && repo.existsByCorreo(datos.getCorreo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un usuario con ese correo");
        }
        actual.setNombre(datos.getNombre());
        actual.setCorreo(datos.getCorreo());
        return ResponseEntity.ok(repo.save(actual));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
