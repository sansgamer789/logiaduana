
package com.logiaduana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.logiaduana.repository.ShipmentRepository;

@Controller
@RequestMapping("/tracking")
public class ShipmentController {
    private final ShipmentRepository shipmentRepo;
    public ShipmentController(ShipmentRepository shipmentRepo) { this.shipmentRepo = shipmentRepo; }

    @GetMapping("/")
    public String list(Model m) {
        m.addAttribute("shipments", shipmentRepo.findAll());
        return "tracking/list";
    }
}
