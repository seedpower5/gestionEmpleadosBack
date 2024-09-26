package com.jorge.gestionEmpleadosBack.controlador;

import com.jorge.gestionEmpleadosBack.modelo.Empleado;
import com.jorge.gestionEmpleadosBack.repositorio.EmpleadoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmpleadoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EmpleadoRepositorio repositorio;

    @InjectMocks
    private EmpleadoControlador empleadoControlador;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(empleadoControlador).build();
        empleado = new Empleado(1L, "Juan", "Desarrollador", "juan@example.com", 40.0f); // Asegúrate de incluir horasTrabajadas
    }

    @Test
    void listarTodosLosEmpleados() throws Exception {
        List<Empleado> empleados = new ArrayList<>();
        empleados.add(empleado);

        when(repositorio.findAll()).thenReturn(empleados);

        mockMvc.perform(get("/api/v1/empleados"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].horasTrabajadas").value(40.0f)); // Verificación de horasTrabajadas
    }

    @Test
    void guardarEmpleado() throws Exception {
        when(repositorio.existsByEmail(any(String.class))).thenReturn(false);
        when(repositorio.save(any(Empleado.class))).thenReturn(empleado);

        mockMvc.perform(post("/api/v1/empleados")
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"apellido\": \"Desarrollador\", \"email\": \"juan@example.com\", \"horasTrabajadas\": 40.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.horasTrabajadas").value(40.0f));
    }

    @Test
    void guardarEmpleado_EmailExistente() throws Exception {
        when(repositorio.existsByEmail(any(String.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/empleados")
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"apellido\": \"Desarrollador\", \"email\": \"juan@example.com\", \"horasTrabajadas\": 40.0}"))
                .andExpect(status().isConflict()); // Espera un código 409
    }

    @Test
    void guardarEmpleado_HorasTrabajadasNegativas() throws Exception {
        when(repositorio.existsByEmail(any(String.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/empleados")
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"apellido\": \"Desarrollador\", \"email\": \"juan@example.com\", \"horasTrabajadas\": -5.0}"))
                .andExpect(status().isBadRequest()); // Espera un código 400 para horas trabajadas negativas
    }

    @Test
    void eliminarEmpleado() throws Exception {
        Long empleadoId = 1L;
        when(repositorio.existsById(empleadoId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/empleados/" + empleadoId))
                .andExpect(status().isNoContent());

        verify(repositorio, times(1)).deleteById(empleadoId);
    }

    @Test
    void eliminarEmpleado_NoEncontrado() throws Exception {
        Long empleadoId = 1L;
        when(repositorio.existsById(empleadoId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/empleados/" + empleadoId))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarEmpleado() throws Exception {
        Long empleadoId = 1L;
        when(repositorio.findById(empleadoId)).thenReturn(java.util.Optional.ofNullable(empleado));
        when(repositorio.save(any(Empleado.class))).thenReturn(empleado);

        mockMvc.perform(put("/api/v1/empleados/" + empleadoId)
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"apellido\": \"Desarrollador\", \"email\": \"juan_nuevo@example.com\", \"horasTrabajadas\": 45.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.horasTrabajadas").value(45.0f));
    }

    @Test
    void actualizarEmpleado_HorasTrabajadasNegativas() throws Exception {
        Long empleadoId = 1L;
        when(repositorio.findById(empleadoId)).thenReturn(java.util.Optional.ofNullable(empleado));

        mockMvc.perform(put("/api/v1/empleados/" + empleadoId)
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"apellido\": \"Desarrollador\", \"email\": \"juan_nuevo@example.com\", \"horasTrabajadas\": -10.0}"))
                .andExpect(status().isBadRequest()); // Espera un código 400 por horas trabajadas negativas
    }
}
