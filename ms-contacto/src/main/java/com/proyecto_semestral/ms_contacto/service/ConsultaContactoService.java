package com.proyecto_semestral.ms_contacto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_contacto.dto.request.CrearConsultaRequest;
import com.proyecto_semestral.ms_contacto.dto.response.ConsultaResponse;
import com.proyecto_semestral.ms_contacto.entity.ConsultaContacto;
import com.proyecto_semestral.ms_contacto.repository.ConsultaContactoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaContactoService {

    private final ConsultaContactoRepository consultaRepository;

    @Transactional
    public ConsultaResponse crear(CrearConsultaRequest request) {
        ConsultaContacto consulta = ConsultaContacto.builder()
                .usuarioId(request.usuarioId())
                .email(request.email().trim())
                .telefono(normalizarOpcional(request.telefono()))
                .nombreNegocio(request.nombreNegocio().trim())
                .tipoNegocio(normalizarOpcional(request.tipoNegocio()))
                .tamanoNegocio(normalizarOpcional(request.tamanoNegocio()))
                .descripcion(request.descripcion().trim())
                .build();

        return toResponse(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public ConsultaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listar() {
        return consultaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        consultaRepository.delete(buscar(id));
    }

    private ConsultaContacto buscar(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Consulta de contacto no encontrada con id " + id));
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private ConsultaResponse toResponse(ConsultaContacto consulta) {
        return ConsultaResponse.builder()
                .id(consulta.getId())
                .usuarioId(consulta.getUsuarioId())
                .email(consulta.getEmail())
                .telefono(consulta.getTelefono())
                .nombreNegocio(consulta.getNombreNegocio())
                .tipoNegocio(consulta.getTipoNegocio())
                .tamanoNegocio(consulta.getTamanoNegocio())
                .descripcion(consulta.getDescripcion())
                .estado(consulta.getEstado())
                .creadoEn(consulta.getCreadoEn())
                .build();
    }
}
