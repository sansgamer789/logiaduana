package com.logiaduana.controller;

import com.logiaduana.repository.CargaRepository;
import com.logiaduana.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalCargas", cargaRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "dashboard"; // dashboard.html
    }
}
