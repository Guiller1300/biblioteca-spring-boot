package com.arias.biblioteca_arias13;

import com.arias.biblioteca_arias13.libro.LibroRepository;
import com.arias.biblioteca_arias13.prestamo.PrestamoRepository;
import com.arias.biblioteca_arias13.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final LibroRepository libroRepo;
    private final UsuarioRepository usuarioRepo;
    private final PrestamoRepository prestamoRepo;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalLibros", libroRepo.count());
        model.addAttribute("totalUsuarios", usuarioRepo.count());
        model.addAttribute("prestamosActivos", prestamoRepo.findByFechaDevolucionIsNull().size());
        return "index";
    }
}
