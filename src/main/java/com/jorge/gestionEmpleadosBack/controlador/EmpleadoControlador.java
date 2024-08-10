package com.jorge.gestionEmpleadosBack.controlador;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.jorge.gestionEmpleadosBack.modelo.Empleado;
import com.jorge.gestionEmpleadosBack.repositorio.EmpleadoRepositorio;


@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoControlador
{
    @Autowired
    private EmpleadoRepositorio repositorio;
    @GetMapping("/empleados")
    //metodo para listar todos los empleados
    public List<Empleado> listarTodosLosEmpleados()
    {
        return repositorio.findAll();
    }
    @PostMapping("/empleados")
    //metodo para guardar empleado
    public Empleado guardarEmpleado(@RequestBody Empleado empleado)
    {
        return repositorio.save(empleado);
    }

}
