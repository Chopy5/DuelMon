package gal.usc.etse.es.restdemo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("dex") //RequestMapping define la ruta base para toda la clase
public class Controller {
    private final DexService dexService;

    //Autowired especifica que la clase necesita una instancia de un Service en este caso
    //Al arrancar, Spring hace una pasada por todo el codigo comprobando las anotaciones, generando un registro de definicionesç
    //En esta caso, la anotacion @Service sigue el patron Singleton
    //entonces Spring la instanciara y se la pasara a los constructores que la necesiten y usen @Autowired
    @Autowired
    public Controller(DexService dexService) {
        this.dexService = dexService;
    }

    @GetMapping("species")  //GetMapping es para rutas de endpoints concretos, en este caso GET
    public ResponseEntity<@NonNull Set<Pokemon>> getPokemons(   //Devuelve un set de pokemon, nunca puede ser null el set -> CASI SIEMPRE
        @RequestParam(value = "type", required = false, defaultValue = "") String type) { //parametro opcional de tipo del query, String
        // Devuelve (return) 200 OK con el conjunto resultante
        // Se podria añadir validacion y devolver otras cosas
        // AUnque arriba definiésemos un set, si no se devuelve cuerpo no hace falta ningún set
        return ResponseEntity.ok(this.dexService.getPokemons(type)); // <-- Minúscula: usa la instancia inyectada
    }





}