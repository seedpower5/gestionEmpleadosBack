package com.jorge.gestionEmpleadosBack.controlador;

import com.jorge.gestionEmpleadosBack.modelo.Empleado;  // Asegúrate de que este es el modelo correcto
import com.jorge.gestionEmpleadosBack.repositorio.EmpleadoRepositorio;  // Asegúrate de que este es el repositorio correcto
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
        mockMvc = MockMvcBuilders.standaloneSetup(empleadoControlador).build(); // Inicializa MockMvc
        empleado = new Empleado(1L, "Juan", "Desarrollador", "juan@example.com"); // Asegúrate de que el constructor tenga todos los parámetros necesarios
    }

    @Test
    void listarTodosLosEmpleados() throws Exception {
        List<Empleado> empleados = new ArrayList<>();
        empleados.add(empleado);

        when(repositorio.findAll()).thenReturn(empleados); // Simular el comportamiento del repositorio

        mockMvc.perform(get("/api/v1/empleados")) // La ruta según tu controlador
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].nombre").value("Juan")); // Suponiendo que 'nombre' es un atributo de Empleado
    }

    @Test
    void guardarEmpleado() throws Exception {
        when(repositorio.existsByEmail(any(String.class))).thenReturn(false); // Simular que el email no existe
        when(repositorio.save(any(Empleado.class))).thenReturn(empleado); // Simular el comportamiento del repositorio

        mockMvc.perform(post("/api/v1/empleados") // La ruta según tu controlador
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"puesto\": \"Desarrollador\", \"email\": \"juan@example.com\"}")) // JSON del nuevo empleado
                .andExpect(status().isCreated());
    }

    @Test
    void guardarEmpleado_EmailExistente() throws Exception {
        when(repositorio.existsByEmail(any(String.class))).thenReturn(true); // Simular que el email ya existe

        mockMvc.perform(post("/api/v1/empleados") // La ruta según tu controlador
                        .contentType("application/json")
                        .content("{\"nombre\": \"Juan\", \"puesto\": \"Desarrollador\", \"email\": \"juan@example.com\"}")) // JSON del nuevo empleado
                .andExpect(status().isConflict()); // Espera un código 409
    }

    @Test
    void eliminarEmpleado() throws Exception {
        Long empleadoId = 1L;
        when(repositorio.existsById(empleadoId)).thenReturn(true); // Simular que el empleado existe

        mockMvc.perform(delete("/api/v1/empleados/" + empleadoId)) // La ruta según tu controlador
                .andExpect(status().isNoContent()); // Espera un código 204

        verify(repositorio, times(1)).deleteById(empleadoId); // Verifica que se haya llamado al método delete
    }

    @Test
    void eliminarEmpleado_NoEncontrado() throws Exception {
        Long empleadoId = 1L;
        when(repositorio.existsById(empleadoId)).thenReturn(false); // Simular que el empleado no existe

        mockMvc.perform(delete("/api/v1/empleados/" + empleadoId)) // La ruta según tu controlador
                .andExpect(status().isNotFound()); // Espera un código 404
    }
}
