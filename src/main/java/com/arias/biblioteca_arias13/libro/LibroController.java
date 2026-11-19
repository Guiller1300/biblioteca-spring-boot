package com.arias.biblioteca_arias13.libro;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroRepository repo;

    // GET /api/libros/listar
    @GetMapping("/listar")
    public List<Libro> listar() {
        return repo.findAll();
    }

    // GET /api/libros/obtener/1
    @GetMapping("/obtener/{id}")
    public ResponseEntity<Libro> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/libros/buscar?titulo=quijote
    @GetMapping("/buscar")
    public List<Libro> buscar(@RequestParam String titulo) {
        return repo.findByTituloContainingIgnoreCase(titulo);
    }

    // POST /api/libros/crear
    @PostMapping("/crear")
    public ResponseEntity<Libro> crear(@Valid @RequestBody Libro libro) {
        libro.setId(null);
        libro.setPrestado(false);
        Libro guardado = repo.save(libro);
        return ResponseEntity
                .created(URI.create("/api/libros/obtener/" + guardado.getId()))
                .body(guardado);
    }

    // PUT /api/libros/actualizar/1
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Libro> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Libro datos) {
        Libro actual = repo.findById(id).orElse(null);
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }
        actual.setTitulo(datos.getTitulo());
        actual.setAutor(datos.getAutor());
        actual.setCategoria(datos.getCategoria());
        return ResponseEntity.ok(repo.save(actual));
    }

    // DELETE /api/libros/eliminar/1
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Libro actual = repo.findById(id).orElse(null);
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }
        if (actual.isPrestado()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el libro esta prestado");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
