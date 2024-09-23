package com.jorge.gestionEmpleadosBack.controlador;
import java.util.List;
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

    @GetMapping("/empleados")
    // Método para listar todos los empleados
    public List<Empleado> listarTodosLosEmpleados() {
        return repositorio.findAll();
    }

    @PostMapping("/empleados")
    // Método para guardar un nuevo empleado
    public ResponseEntity<Empleado> guardarEmpleado(@RequestBody Empleado empleado) {
        // Verificamos si el email ya existe para evitar duplicados
        if(repositorio.existsByEmail(empleado.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null); // Devuelve código 409 si el email ya está en uso
        }

        // Guardamos el nuevo empleado en la base de datos
        Empleado nuevoEmpleado = repositorio.save(empleado);

        // Devolvemos una respuesta con el empleado creado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @DeleteMapping("/empleados/{id}")
    // Método para eliminar un empleado por ID
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
}