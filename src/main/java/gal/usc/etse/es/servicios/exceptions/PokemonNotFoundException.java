package gal.usc.etse.es.servicios.exceptions;

public class PokemonNotFoundException extends ResourceNotFoundException {
    public PokemonNotFoundException(String message) {
        super(message);
    }
}
