package com.jorge.gestionEmpleadosBack.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//creamos excepciones personalizadas por si no existe ningun empleaado
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException
{
    private static  final long serialVersionId = 1L;
    //creamos un constructor y le pasamos un mensaje
    public ResourceNotFoundException(String mensaje)
    {
        super(mensaje);
    }
}
