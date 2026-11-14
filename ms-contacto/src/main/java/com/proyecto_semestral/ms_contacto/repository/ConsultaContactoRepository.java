package com.proyecto_semestral.ms_contacto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_contacto.entity.ConsultaContacto;

@Repository
public interface ConsultaContactoRepository extends JpaRepository<ConsultaContacto, Long> {
}
