package com.logiaduana.controller;

import com.logiaduana.model.Carga;
import com.logiaduana.repository.CargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class TrackingController {

    @Autowired
    private CargaRepository cargaRepository;

    // Página inicial del tracking
    @GetMapping("/tracking")
    public String trackingForm() {
        return "tracking";  // archivo tracking.html en templates
    }

    // Buscar carga por número de guía
    @PostMapping("/tracking")
    public String buscarCarga(@RequestParam("numeroGuia") String numeroGuia, Model model) {
        Optional<Carga> carga = cargaRepository.findByNumeroGuia(numeroGuia);

        if (carga.isPresent()) {
            model.addAttribute("carga", carga.get());
        } else {
            model.addAttribute("error", "No se encontró ninguna carga con ese número de guía.");
        }

        return "tracking";
    }
}
