package gal.usc.etse.es.servicios.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /*
        Clase manejadora de excepciones:
            -Spring crea inicialmente un diccionario de manejadores de excepciones en base al @RestControllerAdvice
            -Spring captuta las excepciones que salgan del controller, y las manda a su manejador asignado en el diccionario
     */

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
}
