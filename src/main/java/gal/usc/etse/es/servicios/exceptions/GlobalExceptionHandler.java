package gal.usc.etse.es.servicios.exceptions;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /*
        Clase manejadora de excepciones:
            -Spring crea inicialmente un diccionario de manejadores de excepciones en base al @RestControllerAdvice
            -Spring captuta las excepciones que salgan del controller, y las manda a su manejador asignado en el diccionario
     */

    // Manejador genérico -> para lo que no entre en ninguna clase específica, SPRING por defecto elige el manejador más específico, NO HAY PROBLEMA CON EL ORDEN
    // Así nos aseguramos de que Spring no revela demasiada informaición
    // Así usamos el RFC
    // Si no capturamos nada nosotros, escapa de Spring y la captura Tomcat, más lento
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {

        // LOGGEAMOS???? -> es un error del servidor

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,"Ha ocurrido un error inesperado en el servidor");
        problem.setTitle("Error interno del servidor");
        problem.setType(URI.create("https://duelmon.usc.gal/errors/internal-server-error"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalidInput(InvalidInputException ex) {

        // Se crea un ProblemDetail con el STATUS y el DETAIL (el mensaje)
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        // Define TITLE, la URI de TYPE que lleva a la página del error y el TIMESTAMP
        problem.setTitle("Petición incorrecta");
        problem.setType(URI.create("https://duelmon.usc.gal/errors/bad-request"));
        problem.setProperty("timestamp", Instant.now());

        // El ENDPOINT ya lo obtiene Spring de la propia petición y lo fija en el ProblemDetail
        return problem;
    }

    // PokemonNotFoundException y MoveNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problem.setTitle("Recurso no encontrado");
        problem.setType(URI.create("https://duelmon.usc.gal/errors/not-found"));
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }


    // Manejador para fallos en la conversión de los enums pasados como parámetro -> evitamos usar el por defecto que no es útil para el usuario
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        // Cogemos el tipo esperado
        Class<?> requiredType = ex.getRequiredType();

        String detail;

        // Si es de tipo enum le mostramos las opciones válidas
        if (requiredType != null && requiredType.isEnum()) {
            // Coge los valores del enum
            Object[] allowed = requiredType.getEnumConstants();

            // Guarda el texto en el detail
            detail = String.format("El parámetro '%s' recibió '%s'. Valores aceptados: %s", ex.getName(), ex.getValue(), Arrays.toString(allowed));

        } else {    // Del resto de cosas no damos información -> mejor que no lo sepa el exterior
            detail = String.format("Formato no válido para el parámetro '%s'", ex.getName());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                detail
        );

        problem.setTitle("Parámetro no válido");
        problem.setType(URI.create("https://duelmon.usc.gal/errors/type-mismatch"));
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    // Manejador para excepciones de validación de los records
    // El por defecto de Spring no sigue el RFC ni le indica los campos que fallaron
    // Sobreescribimos el método de ResponseEntityExceptionHandler -> Spring ya manejaba estas excepciones
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // Crea un mapa con cada campo y sus errores
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // Creamos el problem y guardamos los errores
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
            "La validación del cuerpo de la petición ha fallado"
        );

        problem.setTitle("Parámetro no válido");
        problem.setType(URI.create("https://duelmon.usc.gal/errors/validation-error"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("invalidFields", errors);


        // Devolvemos la respuesta con el problem como body -> ACCEPT
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    // Y PARA LOS PARAMETROS DE LA URL NO HACE FALTA???
}
