package com.jorge.gestionEmpleadosBack.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jorge.gestionEmpleadosBack.modelo.Empleado;
@Repository
public interface EmpleadoRepositorio extends JpaRepository<Empleado,Long>
{
    // Método para verificar si un email ya existe
    boolean existsByEmail(String email);
}
