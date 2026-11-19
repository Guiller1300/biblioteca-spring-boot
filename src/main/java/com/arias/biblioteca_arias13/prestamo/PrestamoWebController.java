package com.arias.biblioteca_arias13.prestamo;

import com.arias.biblioteca_arias13.libro.Libro;
import com.arias.biblioteca_arias13.libro.LibroRepository;
import com.arias.biblioteca_arias13.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/prestamos")
@RequiredArgsConstructor
public class PrestamoWebController {

    private final PrestamoRepository repo;
    private final LibroRepository libroRepo;
    private final com.arias.biblioteca_arias13.usuario.UsuarioRepository usuarioRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("prestamos", repo.findAll());
        return "prestamos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("librosDisponibles", libroRepo.findByPrestadoFalse());
        model.addAttribute("usuarios", usuarioRepo.findAll());
        return "prestamos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long libroId, @RequestParam Long usuarioId) {
        Libro libro = libroRepo.findById(libroId).orElse(null);
        Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
        if (libro == null || usuario == null || libro.isPrestado()) {
            return "redirect:/prestamos";
        }
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(null);
        libro.setPrestado(true);
        libroRepo.save(libro);
        repo.save(prestamo);
        return "redirect:/prestamos";
    }

    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id) {
        Prestamo prestamo = repo.findById(id).orElse(null);
        if (prestamo != null && prestamo.getFechaDevolucion() == null) {
            prestamo.setFechaDevolucion(LocalDate.now());
            Libro libro = prestamo.getLibro();
            libro.setPrestado(false);
            libroRepo.save(libro);
            repo.save(prestamo);
        }
        return "redirect:/prestamos";
    }
}
