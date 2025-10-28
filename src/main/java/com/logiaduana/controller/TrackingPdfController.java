package com.logiaduana.controller;

import com.logiaduana.model.Carga;
import com.logiaduana.repository.CargaRepository;
import com.logiaduana.service.PdfService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class TrackingPdfController {

    private final CargaRepository cargaRepository;
    private final PdfService pdfService;

    public TrackingPdfController(CargaRepository cargaRepository, PdfService pdfService) {
        this.cargaRepository = cargaRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/tracking/pdf")
    public ResponseEntity<byte[]> trackingPdf(
            @RequestParam String numeroGuia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        Optional<Carga> cargaOpt = cargaRepository.findByNumeroGuia(numeroGuia);
        Map<String, Object> model = new HashMap<>();
        model.put("numeroGuia", numeroGuia);
        model.put("carga", cargaOpt.orElse(null));
        model.put("desde", desde);
        model.put("hasta", hasta);

        byte[] pdf = pdfService.render("tracking-pdf", model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=tracking-" + numeroGuia + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
