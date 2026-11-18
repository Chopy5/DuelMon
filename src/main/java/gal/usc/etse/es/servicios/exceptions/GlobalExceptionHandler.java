package gal.usc.etse.es.servicios.exceptions;

import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.slf4j.Logger;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    // Logs para errores internos
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Prefijo para todos los errores de la API
    private static final String TYPE_BASE = "urn:duelmon:error:";

    // Contiene el campo y el error que sucedió
    public record InvalidField(String field, String message) {}

    // Metodo para crear un problem detail con los datos completos
    private static ProblemDetail createProblem(HttpStatus status, String title, String type, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(TYPE_BASE + type));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }


    /*
        Clase manejadora de excepciones:
            -Spring crea inicialmente un diccionario de manejadores de excepciones en base al @RestControllerAdvice
            -Spring captuta las excepciones que salgan del controller, y las manda a su manejador asignado en el diccionario
     */



    // Manejador genérico -> para lo que no entre en ninguna clase específica
    // SPRING por defecto elige el manejador más específico, si hay otro más específico la excepción va a ese antes que a este
    // Se registra la traza completa en el servidor, y al cliente solo se le da un mensaje neutro
    // Si no capturamos nada nosotros, el error lo generaría el contenedor con otro formato
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {

        // Registramos el error y la traza completa para depuración en el servidor
        log.error("Excepción no controlada en el servidor: ", ex);
        return createProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor",
                "internal-server-error", "Ha ocurrido un error inesperado en el servidor");
    }

    // ---PARÁMETROS EN LA URL---

    // Manejador para fallos en la conversión de tipos de los parámetros de la URL
    // Debe sacar un 400, fallos sintácticos
    // No tiene manejador por defecto -> la respuesta por defecto de Spring muestra trazas internas que revelan detalles de implementación
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Throwable cause = ex.getMostSpecificCause();

        // Comprobamos si es concretamente un enum, en cuyo caso cogemos el mensaje que nos daba
        String detail = (cause instanceof InvalidEnumValueException) ? cause.getMessage() : String.format("Formato no válido para el parámetro '%s'", ex.getName());

        return createProblem(HttpStatus.BAD_REQUEST, "Parámetro no válido", "type-mismatch", detail);
    }

    // Fallos de validacion en los parámetros tomados de la URL -> si tienen restricciones como @Size...
    // Debe sacar un 400, son los parámetros de la URL, no el cuerpo
    // El por defecto de Spring saca 400, y no explicita los parámetros concretos que fallan y por qué
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // Crea lista con errores para cada parametro
        List<InvalidField> errors = new ArrayList<>();
        ex.getParameterValidationResults().forEach(result -> {
            String paramName = result.getMethodParameter().getParameterName();
            String field = paramName != null ? paramName : "desconocido";

            result.getResolvableErrors().forEach(error -> {
                errors.add(new InvalidField(
                        field,
                        error.getDefaultMessage() != null ? error.getDefaultMessage() : "valor no válido"
                ));
            });
        });

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Parámetros de consulta no válidos",
                "query-validation-error",
                "La validación de los parámetros de la URL ha fallado"
        );
        problem.setProperty("invalidParams", errors);

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    // ---CUERPO---

    // Manejador para fallos de lectura/deserialización del JSON del cuerpo
    // Devuelve 400
    // POr defecto Spring no especifica suficiente
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Cuerpo de petición mal formado",
                "malformed-body",
                "El cuerpo de la petición no tiene un formato JSON válido o contiene datos ilegibles"
        );

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    // Manejador para excepciones de validación en el cuerpo -> si tienen restricciones como @Size...
    // Debe sacar un 422, fallos semánticos
    // El por defecto de Spring saca 400, y no explicita los campos concretos que fallan y por qué
    // Sobreescribimos el método de ResponseEntityExceptionHandler
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // Crea una lista de errores, con los errores de cada campo
        List<InvalidField> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(new InvalidField(
                    fieldError.getField(),
                    fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "valor no válido"
            ));
        }

        ProblemDetail problem = createProblem(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Cuerpo de petición no válido",
                "request-body-validation-error",
                "Uno o más campos del cuerpo de la petición no cumplen las restricciones"
        );
        problem.setProperty("invalidFields", errors);

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    // ---REGLAS DE NEGOCIO Y DOMINIO---


    // Manejador para excepciones de validación en las entidades JPA -> cuando una entrada es sintácticamente correcta para el controlador, pero viola una regla lógica del sistema
    // Devuelve 400
    // Son excepciones personalizadas, que Spring no maneja
    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalidInput(InvalidInputException ex) {
        return createProblem(HttpStatus.BAD_REQUEST, "Petición incorrecta", "bad-request", ex.getMessage());
    }

    // Manejador para excepciones de recursos no encontrados
    // Devuelve 404
    // Spring por defecto no tiene manejador para nuestras excepciones personalizadas
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        return createProblem(HttpStatus.NOT_FOUND, "Recurso no encontrado", "not-found", ex.getMessage());
    }

    /*
    // Manejador para conflictos de recursos o restricciones únicas de base de datos
    // Devuelve 409 Conflict
    @ExceptionHandler({DataIntegrityViolationException.class, DuplicateResourceException.class})
    public ResponseEntity<ProblemDetail> handleConflict(Exception ex) {
        String detail = "El recurso ya existe o entra en conflicto con los datos actuales";
        if (ex instanceof DuplicateResourceException) {
            detail = ex.getMessage();
        }

        ProblemDetail problem = createProblem(HttpStatus.CONFLICT, "Conflicto con el recurso", "resource-conflict", detail);
        return ResponseEntity.status(problem.getStatus()).body(problem);
    }*/
}
