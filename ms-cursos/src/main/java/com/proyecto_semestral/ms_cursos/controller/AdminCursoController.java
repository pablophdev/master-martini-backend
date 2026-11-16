package com.proyecto_semestral.ms_cursos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.dto.response.CursoDetalleResponse;
import com.proyecto_semestral.ms_cursos.service.CursoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/cursos")
@RequiredArgsConstructor
public class AdminCursoController {

    private final CursoService cursoService;

    @GetMapping
    public List<CursoDetalleResponse> listarTodos() {
        return cursoService.listarTodos();
    }
}
