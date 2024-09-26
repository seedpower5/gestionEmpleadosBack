package com.jorge.gestionEmpleadosBack.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jorge.gestionEmpleadosBack.modelo.Empleado;
import com.jorge.gestionEmpleadosBack.repositorio.EmpleadoRepositorio;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoControlador {

    @Autowired
    private EmpleadoRepositorio repositorio;

    // Método para listar todos los empleados
    @GetMapping("/empleados")
    public List<Empleado> listarTodosLosEmpleados() {
        return repositorio.findAll();
    }

    // Método para guardar un nuevo empleado
    @PostMapping("/empleados")
    public ResponseEntity<Empleado> guardarEmpleado(@RequestBody Empleado empleado) {
        // Verificamos si el email ya existe para evitar duplicados
        if (repositorio.existsByEmail(empleado.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Devuelve código 409 si el email ya está en uso
        }

        // Validación adicional: verificar que las horas trabajadas no sean negativas
        if (empleado.getHorasTrabajadas() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Código 400 si las horas trabajadas son inválidas
        }

        // Guardamos el nuevo empleado en la base de datos
        Empleado nuevoEmpleado = repositorio.save(empleado);

        // Devolvemos una respuesta con el empleado creado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    // Método para eliminar un empleado por ID
    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        // Verificamos si el empleado con el ID dado existe
        if (!repositorio.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Devuelve código 404 si no se encuentra el empleado
        }

        // Eliminamos el empleado
        repositorio.deleteById(id);

        // Devolvemos una respuesta con código 204 No Content
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Método para obtener un empleado por ID
    @GetMapping("/empleados/{id}")
    public ResponseEntity<Empleado> obtenerEmpleadoPorId(@PathVariable Long id) {
        Optional<Empleado> empleado = repositorio.findById(id);

        if (empleado.isPresent()) {
            return ResponseEntity.ok(empleado.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Método para actualizar un empleado por ID
    @PutMapping("/empleados/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable Long id, @RequestBody Empleado detallesEmpleado) {
        Optional<Empleado> empleadoExistente = repositorio.findById(id);

        if (empleadoExistente.isPresent()) {
            Empleado empleado = empleadoExistente.get();
            empleado.setNombre(detallesEmpleado.getNombre());
            empleado.setApellido(detallesEmpleado.getApellido());
            empleado.setEmail(detallesEmpleado.getEmail());
            empleado.setHorasTrabajadas(detallesEmpleado.getHorasTrabajadas());

            // Verificamos si el email nuevo ya existe (excepto para el empleado actual)
            if (!empleado.getEmail().equals(detallesEmpleado.getEmail()) && repositorio.existsByEmail(detallesEmpleado.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Código 409 si el nuevo email ya está en uso
            }

            // Validación de horas trabajadas
            if (empleado.getHorasTrabajadas() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Código 400 si las horas trabajadas son inválidas
            }

            // Guardamos los cambios en el empleado
            Empleado empleadoActualizado = repositorio.save(empleado);

            return ResponseEntity.ok(empleadoActualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Código 404 si no se encuentra el empleado
        }
    }
}
