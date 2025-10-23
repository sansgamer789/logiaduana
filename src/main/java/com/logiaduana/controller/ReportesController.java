package com.logiaduana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportesController {

    @GetMapping("/reportes")
    public String verReportes(Model model) {
        model.addAttribute("mensaje", "Página de reportes funcionando!");
        return "reportes"; // busca templates/reportes.html
    }
}
