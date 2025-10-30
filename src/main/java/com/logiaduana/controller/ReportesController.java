package com.logiaduana.controller;

import com.logiaduana.model.Carga;
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
 * Controller de reportes — exporta datos en Excel (XLSX)
 * Contiene hojas:
 *  - Cargas
 *  - Tracking / TrackingEvent
 *  - Usuarios (solo nombre, email y rol)
 */
@Controller
public class ReportesController {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private EntityManager entityManager;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/reportes")
    public String verReportes(Model model) {
        model.addAttribute("mensaje", "Página de reportes funcionando!");
        return "reportes";
    }

    @GetMapping("/reportes/exportar-excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        String fileName = "reportes_logiaduana.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook workbook = new XSSFWorkbook()) {

            // Estilos
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // ---------- HOJA CARGAS ----------
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
                r.createCell(0).setCellValue(c.getId() != null ? c.getId() : 0);
                r.createCell(1).setCellValue(c.getNumeroGuia() != null ? c.getNumeroGuia() : "");
                r.createCell(2).setCellValue(c.getCliente() != null ? c.getCliente() : "");
                r.createCell(3).setCellValue(c.getDescripcion() != null ? c.getDescripcion() : "");
                r.createCell(4).setCellValue(c.getEstado() != null ? c.getEstado() : "");
                try {
                    Object ubic = safeInvoke(c, "getUbicacion");
                    Object fecha = safeInvoke(c, "getFechaActualizacion");
                    r.createCell(5).setCellValue(ubic != null ? ubic.toString() : "");
                    r.createCell(6).setCellValue(fecha != null ? fecha.toString() : "");
                } catch (Exception ex) {
                    r.createCell(5).setCellValue("");
                    r.createCell(6).setCellValue("");
                }
            }

            // ---------- HOJA TRACKING ----------
            Sheet sheetTracking = workbook.createSheet("Tracking");
            String[] colsT = {"ID", "Shipment ID", "Carga ID", "Número Guía", "Timestamp", "Status", "Note", "Ubicación"};
            Row headerT = sheetTracking.createRow(0);
            for (int i = 0; i < colsT.length; i++) {
                Cell c = headerT.createCell(i);
                c.setCellValue(colsT[i]);
                c.setCellStyle(headerStyle);
            }

            int rowT = 1;
            try {
                Query qEvents = entityManager.createQuery("SELECT te FROM TrackingEvent te");
                @SuppressWarnings("unchecked")
                List<Object> events = qEvents.getResultList();
                for (Object evObj : events) {
                    Row r = sheetTracking.createRow(rowT++);
                    Long evId = (Long) invokeGetter(evObj, "getId");
                    Object shipmentObj = invokeGetter(evObj, "getShipment");
                    String timestampStr = "";
                    Object ts = invokeGetter(evObj, "getTimestamp");
                    if (ts != null) timestampStr = ts.toString();
                    String status = (String) invokeGetter(evObj, "getStatus");
                    String note = (String) invokeGetter(evObj, "getNote");

                    r.createCell(0).setCellValue(evId != null ? evId : 0);
                    if (shipmentObj != null) {
                        Long shipmentId = (Long) invokeGetter(shipmentObj, "getId");
                        r.createCell(1).setCellValue(shipmentId != null ? shipmentId : 0);
                        Object cargaObj = tryInvoke(shipmentObj, "getCarga", "getCargo");
                        if (cargaObj != null) {
                            Long cargaId = (Long) tryInvokeReturn(shipmentObj, "getCarga", "getCargo", "getId");
                            String numeroGuia = (String) tryInvokeReturn(shipmentObj, "getCarga", "getCargo", "getNumeroGuia");
                            r.createCell(2).setCellValue(cargaId != null ? cargaId : 0);
                            r.createCell(3).setCellValue(numeroGuia != null ? numeroGuia : "");
                        }
                    }
                    r.createCell(4).setCellValue(timestampStr);
                    r.createCell(5).setCellValue(status != null ? status : "");
                    r.createCell(6).setCellValue(note != null ? note : "");
                    try {
                        Object ubic = tryInvokeReturn(evObj, "getShipment", "getCarga", "getUbicacion");
                        r.createCell(7).setCellValue(ubic != null ? ubic.toString() : "");
                    } catch (Exception ex) {
                        r.createCell(7).setCellValue("");
                    }
                }
            } catch (Exception e) {
                // Si no hay TrackingEvent, se ignora
            }

            // ---------- HOJA USUARIOS ----------
            try {
                Query qUsuarios = entityManager.createQuery("SELECT u FROM Usuario u");
                @SuppressWarnings("unchecked")
                List<com.logiaduana.model.Usuario> usuarios = qUsuarios.getResultList();

                Sheet sheetUsuarios = workbook.createSheet("Usuarios");
                String[] colsU = {"ID", "Nombre", "Email", "Rol"};
                Row headerU = sheetUsuarios.createRow(0);
                for (int i = 0; i < colsU.length; i++) {
                    Cell c = headerU.createCell(i);
                    c.setCellValue(colsU[i]);
                    c.setCellStyle(headerStyle);
                }

                int rowU = 1;
                for (com.logiaduana.model.Usuario u : usuarios) {
                    Row r = sheetUsuarios.createRow(rowU++);
                    r.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
                    r.createCell(1).setCellValue(u.getNombre() != null ? u.getNombre() : "");
                    r.createCell(2).setCellValue(u.getEmail() != null ? u.getEmail() : "");
                    r.createCell(3).setCellValue(u.getRol() != null ? u.getRol() : "");
                }

                for (int i = 0; i < colsU.length; i++) {
                    sheetUsuarios.autoSizeColumn(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Ajustar columnas en otras hojas
            for (int i = 0; i < 10; i++) {
                try {
                    sheetCargas.autoSizeColumn(i);
                } catch (Exception ignore) {}
                try {
                    sheetTracking.autoSizeColumn(i);
                } catch (Exception ignore) {}
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    // -----------------------
    // Métodos auxiliares
    // -----------------------
    private Object tryInvoke(Object target, String... getters) {
        for (String g : getters) {
            try {
                Object r = invokeGetter(target, g);
                if (r != null) return r;
            } catch (Exception ignored) {}
        }
        return null;
    }

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

    private static Object invokeGetter(Object obj, String getterName) throws Exception {
        if (obj == null) return null;
        try {
            java.lang.reflect.Method m = obj.getClass().getMethod(getterName);
            return m.invoke(obj);
        } catch (NoSuchMethodException e) {
            try {
                String isName = getterName.replaceFirst("get", "is");
                java.lang.reflect.Method m2 = obj.getClass().getMethod(isName);
                return m2.invoke(obj);
            } catch (Exception ex) {
                throw e;
            }
        }
    }

    private static Object safeInvoke(Object obj, String getterName) {
        try {
            return invokeGetter(obj, getterName);
        } catch (Exception e) {
            return null;
        }
    }
}