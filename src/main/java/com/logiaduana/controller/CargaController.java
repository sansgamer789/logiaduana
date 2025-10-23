package com.logiaduana.controller;

import com.logiaduana.model.Carga;
import com.logiaduana.repository.CargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CargaController {

    @Autowired
    private CargaRepository cargaRepository;

    // Listar todas las cargas
    @GetMapping("/cargas")
    public String listarCargas(Model model) {
        model.addAttribute("cargas", cargaRepository.findAll());
        return "cargas";
    }

    // Formulario nueva carga
    @GetMapping("/cargas/nueva")
    public String nuevaCarga(Model model) {
        model.addAttribute("carga", new Carga());
        return "form_carga";
    }

    // Formulario editar carga
    @GetMapping("/cargas/editar/{id}")
    public String editarCarga(@PathVariable Long id, Model model) {
        Carga carga = cargaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("carga", carga);
        return "form_carga";
    }

    // Guardar carga (nueva o editada)
    @PostMapping("/cargas/guardar")
    public String guardarCarga(@ModelAttribute Carga carga) {
        cargaRepository.save(carga);
        return "redirect:/cargas";
    }

    // Eliminar carga
    @GetMapping("/cargas/eliminar/{id}")
    public String eliminarCarga(@PathVariable Long id) {
        cargaRepository.deleteById(id);
        return "redirect:/cargas";
    }
}

