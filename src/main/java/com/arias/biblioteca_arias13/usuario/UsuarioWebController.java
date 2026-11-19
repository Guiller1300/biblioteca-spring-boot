package com.arias.biblioteca_arias13.usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioWebController {

    private final UsuarioRepository repo;

    // GET /usuarios
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", repo.findAll());
        return "usuarios/lista";
    }

    // GET /usuarios/nuevo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario";
    }

    // POST /usuarios/guardar
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        if (repo.existsByCorreo(usuario.getCorreo())) {
            boolean esElMismo = false;
            if (usuario.getId() != null) {
                Usuario actual = repo.findById(usuario.getId()).orElse(null);
                if (actual != null && actual.getCorreo().equals(usuario.getCorreo())) {
                    esElMismo = true;
                }
            }
            if (!esElMismo) {
                return "redirect:/usuarios";
            }
        }
        repo.save(usuario);
        return "redirect:/usuarios";
    }

    // GET /usuarios/editar/1
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", repo.findById(id).orElse(new Usuario()));
        return "usuarios/formulario";
    }

    // GET /usuarios/eliminar/1
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/usuarios";
    }
}
