package com.arias.biblioteca_arias13.prestamo;

import com.arias.biblioteca_arias13.libro.Libro;
import com.arias.biblioteca_arias13.libro.LibroRepository;
import com.arias.biblioteca_arias13.usuario.Usuario;
import com.arias.biblioteca_arias13.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoRepository repo;
    private final LibroRepository libroRepo;
    private final UsuarioRepository usuarioRepo;

    // GET /api/prestamos/listar
    @GetMapping("/listar")
    public List<Prestamo> listar() {
        return repo.findAll();
    }

    // GET /api/prestamos/obtener/1
    @GetMapping("/obtener/{id}")
    public ResponseEntity<Prestamo> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/prestamos/activos
    @GetMapping("/activos")
    public List<Prestamo> activos() {
        return repo.findByFechaDevolucionIsNull();
    }

    // POST /api/prestamos/prestar
    @PostMapping("/prestar")
    public ResponseEntity<?> prestar(@RequestBody Prestamo datos) {
        if (datos.getLibro() == null || datos.getLibro().getId() == null
                || datos.getUsuario() == null || datos.getUsuario().getId() == null) {
            return ResponseEntity.badRequest()
                    .body("Debes enviar el id del libro y el id del usuario");
        }
        Libro libro = libroRepo.findById(datos.getLibro().getId()).orElse(null);
        if (libro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Libro no encontrado");
        }
        Usuario usuario = usuarioRepo.findById(datos.getUsuario().getId()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        if (libro.isPrestado()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El libro ya esta prestado");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(null);

        libro.setPrestado(true);
        libroRepo.save(libro);

        Prestamo guardado = repo.save(prestamo);
        return ResponseEntity
                .created(URI.create("/api/prestamos/obtener/" + guardado.getId()))
                .body(guardado);
    }

    // PUT /api/prestamos/devolver/1
    @PutMapping("/devolver/{id}")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        Prestamo prestamo = repo.findById(id).orElse(null);
        if (prestamo == null) {
            return ResponseEntity.notFound().build();
        }
        if (prestamo.getFechaDevolucion() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El prestamo ya fue devuelto");
        }
        prestamo.setFechaDevolucion(LocalDate.now());

        Libro libro = prestamo.getLibro();
        libro.setPrestado(false);
        libroRepo.save(libro);

        return ResponseEntity.ok(repo.save(prestamo));
    }

    // DELETE /api/prestamos/eliminar/1
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
