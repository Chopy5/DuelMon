package gal.usc.etse.es.servicios.exceptions;

public class MoveNotFoundException extends ResourceNotFoundException {
    public MoveNotFoundException(String message) {
        super(message);
    }
}
