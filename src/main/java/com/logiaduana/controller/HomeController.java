package com.logiaduana.controller;

import com.logiaduana.repository.CargaRepository;
import com.logiaduana.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        // Contadores para el Dashboard
        long totalCargas = cargaRepository.count();
        long totalUsuarios = usuarioRepository.count();

        model.addAttribute("totalCargas", totalCargas);
        model.addAttribute("totalUsuarios", totalUsuarios);

        return "dashboard"; // archivo dashboard.html en templates
    }
}
