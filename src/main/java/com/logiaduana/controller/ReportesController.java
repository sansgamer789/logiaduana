package com.logiaduana.controller;

import com.logiaduana.model.Carga;
import com.logiaduana.model.Shipment;
import com.logiaduana.repository.CargaRepository;
import com.logiaduana.repository.ShipmentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller de reportes — ahora con exportación Excel (XLSX).
 * Usa los repositorios existentes y EntityManager (no crea nuevos repositorios).
 */
@Controller
public class ReportesController {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private EntityManager entityManager; // para consultar Tracking / TrackingEvent que no tienen repo

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/reportes")
    public String verReportes(Model model) {
        model.addAttribute("mensaje", "Página de reportes funcionando!");
        return "reportes"; // templates/reportes.html
    }

    /**
     * Endpoint para exportar un Excel con:
     *   - Hoja "Cargas"  -> datos de la tabla Carga
     *   - Hoja "Tracking" -> datos de Tracking y TrackingEvent (si existen)
     *
     * No requiere crear nuevos repositorios: usamos cargaRepository y EntityManager.
     */
    @GetMapping("/reportes/exportar-excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        // Headers para forzar descarga
        String fileName = "reportes_logiaduana.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Workbook (XLSX)
        try (Workbook workbook = new XSSFWorkbook()) {

            // Estilo cabeceras
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // ---- Hoja CARGAS ----
            Sheet sheetCargas = workbook.createSheet("Cargas");
            String[] colsC = {"ID", "Número Guía", "Cliente", "Descripción", "Estado", "Ubicación", "Fecha Actualización"};
            Row headerC = sheetCargas.createRow(0);
            for (int i = 0; i < colsC.length; i++) {
                Cell c = headerC.createCell(i);
                c.setCellValue(colsC[i]);
                c.setCellStyle(headerStyle);
            }

            List<Carga> cargas = cargaRepository.findAll();
            int rowIndex = 1;
            for (Carga c : cargas) {
                Row r = sheetCargas.createRow(rowIndex++);
                // Ten en cuenta getters de tu entidad Carga
                r.createCell(0).setCellValue(c.getId() != null ? c.getId() : 0);
                r.createCell(1).setCellValue(c.getNumeroGuia() != null ? c.getNumeroGuia() : "");
                r.createCell(2).setCellValue(c.getCliente() != null ? c.getCliente() : "");
                r.createCell(3).setCellValue(c.getDescripcion() != null ? c.getDescripcion() : "");
                r.createCell(4).setCellValue(c.getEstado() != null ? c.getEstado() : "");
                // Si tu entidad Carga tiene ubicacion/fecha, úsala (si no existen, deja vacío)
                try {
                    // algunos proyectos usan getUbicacion / getFechaActualizacion — se intenta acceder y si falla, se deja vacío
                    Object ubic = safeInvoke(c, "getUbicacion");
                    Object fecha = safeInvoke(c, "getFechaActualizacion");
                    r.createCell(5).setCellValue(ubic != null ? ubic.toString() : "");
                    r.createCell(6).setCellValue(fecha != null ? fecha.toString() : "");
                } catch (Exception ex) {
                    r.createCell(5).setCellValue("");
                    r.createCell(6).setCellValue("");
                }
            }

            // ---- Hoja TRACKING ----
            Sheet sheetTracking = workbook.createSheet("Tracking");
            String[] colsT = {"ID", "Shipment ID", "Carga ID", "Número Guía (Carga)", "Timestamp", "Status", "Note", "Ubicación (Tracking)"};
            Row headerT = sheetTracking.createRow(0);
            for (int i = 0; i < colsT.length; i++) {
                Cell c = headerT.createCell(i);
                c.setCellValue(colsT[i]);
                c.setCellStyle(headerStyle);
            }

            int rowT = 1;
            // 1) Intentamos consultar la entidad TrackingEvent si existe
            try {
                Query qEvents = entityManager.createQuery("SELECT te FROM TrackingEvent te");
                @SuppressWarnings("unchecked")
                List<Object> events = qEvents.getResultList();
                for (Object evObj : events) {
                    // Usamos reflexión ligera para evitar acoplamiento fuerte
                    Row r = sheetTracking.createRow(rowT++);
                    Long evId = (Long) invokeGetter(evObj, "getId");
                    Object shipmentObj = invokeGetter(evObj, "getShipment"); // puede ser null
                    String timestampStr = "";
                    Object ts = invokeGetter(evObj, "getTimestamp");
                    if (ts != null) timestampStr = ts.toString();
                    String status = (String) invokeGetter(evObj, "getStatus");
                    String note = (String) invokeGetter(evObj, "getNote");

                    r.createCell(0).setCellValue(evId != null ? evId : 0);
                    if (shipmentObj != null) {
                        Long shipmentId = (Long) invokeGetter(shipmentObj, "getId");
                        r.createCell(1).setCellValue(shipmentId != null ? shipmentId : 0);
                        // shipment -> carga (puede llamarse getCarga o getCargo)
                        Object cargaObj = tryInvoke(shipmentObj, "getCarga", "getCargo");
                        if (cargaObj != null) {
                            Long cargaId = (Long) tryInvokeReturn(shipmentObj, "getCarga", "getCargo", "getId");
                            String numeroGuia = (String) tryInvokeReturn(shipmentObj, "getCarga", "getCargo", "getNumeroGuia");
                            r.createCell(2).setCellValue(cargaId != null ? cargaId : 0);
                            r.createCell(3).setCellValue(numeroGuia != null ? numeroGuia : "");
                        } else {
                            r.createCell(2).setCellValue("");
                            r.createCell(3).setCellValue("");
                        }
                    } else {
                        r.createCell(1).setCellValue("");
                        r.createCell(2).setCellValue("");
                        r.createCell(3).setCellValue("");
                    }
                    r.createCell(4).setCellValue(timestampStr);
                    r.createCell(5).setCellValue(status != null ? status : "");
                    r.createCell(6).setCellValue(note != null ? note : "");
                    // TrackingEvent no siempre tiene 'ubicacion' pero por si acaso
                    try {
                        Object ubic = tryInvokeReturn(evObj, "getShipment", "getCarga", "getUbicacion");
                        r.createCell(7).setCellValue(ubic != null ? ubic.toString() : "");
                    } catch (Exception ex) {
                        r.createCell(7).setCellValue("");
                    }
                }
            } catch (Exception e) {
                // Si no existe TrackingEvent, intentamos la entidad Tracking directa (nombre Tracking)
                try {
                    Query qTracking = entityManager.createQuery("SELECT t FROM Tracking t");
                    @SuppressWarnings("unchecked")
                    List<Object> trackings = qTracking.getResultList();
                    for (Object tObj : trackings) {
                        Row r = sheetTracking.createRow(rowT++);
                        Long tid = (Long) invokeGetter(tObj, "getId");
                        String estado = (String) tryInvokeReturn(tObj, "getEstado", "getStatus");
                        String ubic = (String) tryInvokeReturn(tObj, "getUbicacion", "getLocation");
                        Object fecha = tryInvoke(tObj, "getFechaActualizacion", "getTimestamp", "getLastUpdate");
                        // intentar relacion con carga
                        Object cargaObj = tryInvokeReturnObj(tObj, "getCarga", "getCarga");
                        Long cargaId = null;
                        String numeroGuia = "";
                        if (cargaObj != null) {
                            cargaId = (Long) tryInvokeReturn(cargaObj, "getId");
                            numeroGuia = (String) tryInvokeReturn(cargaObj, "getNumeroGuia", "getNumero");
                        }

                        r.createCell(0).setCellValue(tid != null ? tid : 0);
                        r.createCell(1).setCellValue(""); // shipment id no disponible
                        r.createCell(2).setCellValue(cargaId != null ? cargaId : 0);
                        r.createCell(3).setCellValue(numeroGuia != null ? numeroGuia : "");
                        r.createCell(4).setCellValue(fecha != null ? fecha.toString() : "");
                        r.createCell(5).setCellValue(estado != null ? estado : "");
                        r.createCell(6).setCellValue("");
                        r.createCell(7).setCellValue(ubic != null ? ubic : "");
                    }
                } catch (Exception ex2) {
                    // no hay tracking en BD o no se puede leer -> hoja vacía
                }
            }

            // Auto-size (primeras 10 columnas)
            for (int i = 0; i < 10; i++) {
                try {
                    sheetCargas.autoSizeColumn(i);
                } catch (Exception ignore) {}
                try {
                    sheetTracking.autoSizeColumn(i);
                } catch (Exception ignore) {}
            }

            // Escribir workbook al output stream de la respuesta
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } // workbook cerrado automáticamente
    }

    // -----------------------
    // Utilidades de reflexión segura (evita crear repositorios nuevos; maneja nombres distintos)
    // -----------------------

    /**
     * Intenta invocar getters en cadena: p.ej tryInvoke(obj, "getCarga", "getCargo") -> devuelve el primer no-nulo
     */
    private Object tryInvoke(Object target, String... getters) {
        for (String g : getters) {
            try {
                Object r = invokeGetter(target, g);
                if (r != null) return r;
            } catch (Exception ignored) {}
        }
        return null;
    }

    /**
     * Intenta invocar una cadena de getters y devuelve el último valor (uso: getShipment->getCarga->getId)
     */
    private Object tryInvokeReturn(Object startObj, String... chain) {
        Object cur = startObj;
        try {
            for (String method : chain) {
                if (cur == null) return null;
                cur = invokeGetter(cur, method);
            }
            return cur;
        } catch (Exception e) {
            return null;
        }
    }

    private Object tryInvokeReturnObj(Object startObj, String... chain) {
        return tryInvokeReturn(startObj, chain);
    }

    /**
     * Invoca un getter simple (p.ej "getId", "getEstado")
     */
    private static Object invokeGetter(Object obj, String getterName) throws Exception {
        if (obj == null) return null;
        try {
            java.lang.reflect.Method m = obj.getClass().getMethod(getterName);
            return m.invoke(obj);
        } catch (NoSuchMethodException e) {
            // intentar versión isX para booleanos
            try {
                String isName = getterName.replaceFirst("get", "is");
                java.lang.reflect.Method m2 = obj.getClass().getMethod(isName);
                return m2.invoke(obj);
            } catch (Exception ex) {
                throw e;
            }
        }
    }

    /**
     * Método auxiliar que intenta invocar y no lanza excepción (retorna null en fallo)
     */
    private static Object safeInvoke(Object obj, String getterName) {
        try {
            return invokeGetter(obj, getterName);
        } catch (Exception e) {
            return null;
        }
    }
}
