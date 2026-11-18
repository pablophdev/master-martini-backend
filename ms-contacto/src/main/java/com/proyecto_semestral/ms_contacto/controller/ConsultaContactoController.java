package com.proyecto_semestral.ms_contacto.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_contacto.dto.request.ActualizarEstadoConsultaRequest;
import com.proyecto_semestral.ms_contacto.dto.request.CrearConsultaRequest;
import com.proyecto_semestral.ms_contacto.dto.response.ConsultaResponse;
import com.proyecto_semestral.ms_contacto.service.ConsultaContactoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ConsultaContactoController {

    private final ConsultaContactoService consultaService;

    @PostMapping("/api/contacto")
    public ResponseEntity<ConsultaResponse> crear(
            @Valid @RequestBody CrearConsultaRequest request) {
        ConsultaResponse consulta = consultaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/consultas/" + consulta.id()))
                .body(consulta);
    }

    @GetMapping("/api/consultas")
    public List<ConsultaResponse> listar() {
        return consultaService.listar();
    }

    @GetMapping("/api/consultas/{id}")
    public ConsultaResponse obtener(@PathVariable Long id) {
        return consultaService.obtener(id);
    }

    @PatchMapping("/api/consultas/{id}/estado")
    public ConsultaResponse actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoConsultaRequest request) {
        return consultaService.actualizarEstado(id, request.estado());
    }

    @DeleteMapping("/api/consultas/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        consultaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
