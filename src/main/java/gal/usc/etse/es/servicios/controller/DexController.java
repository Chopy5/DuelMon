package gal.usc.etse.es.servicios.controller;
import gal.usc.etse.es.servicios.model.dto.MoveClass;
import gal.usc.etse.es.servicios.model.dto.PokemonType;
import gal.usc.etse.es.servicios.model.entity.MovementData;
import gal.usc.etse.es.servicios.model.entity.PokemonData;
import gal.usc.etse.es.servicios.service.DexService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dex") //RequestMapping define la ruta base para toda la clase
public class DexController {
    private final DexService dexService;

    //Autowired especifica que la clase necesita una instancia de un Service en este caso
    //Al arrancar, Spring hace una pasada por todo el codigo comprobando las anotaciones, generando un registro de definicionesç
    //En esta caso, la anotacion @Service sigue el patron Singleton
    //entonces Spring la instanciara y se la pasara a los constructores que la necesiten y usen @Autowired

    @Autowired //No se necesita si hay un solo constructor pero bueno
    public DexController(DexService dexService) {
        this.dexService = dexService;
    }

    @GetMapping("/species")  //GetMapping es para rutas de endpoints concretos, en este caso GET
    public ResponseEntity<@NonNull List<PokemonData>> getPokemons(   //Devuelve una list de pokemon, nunca puede ser null -> CASI SIEMPRE
                                                                     @RequestParam(value = "prefix", required = false, defaultValue = "") @NonNull String prefix,
                                                                     @RequestParam(value = "type", required = false) @Nullable PokemonType type) { //parametro opcional de tipo del query, String
        // Devuelve (return) 200 OK con el conjunto resultante
        // Se podria añadir validacion y devolver otras cosas
        // AUnque arriba definiésemos un set, si no se devuelve cuerpo no hace falta ningún set

        return ResponseEntity.ok(this.dexService.searchPokemon(prefix,type));
    }

    @GetMapping("/species/{idPokemon}")
    public ResponseEntity<@NonNull PokemonData> getPokemon(
        @PathVariable("idPokemon") @NonNull String idPokemon) {   //Tiene que tener mismo nombre que en la URL

        return ResponseEntity.ok(this.dexService.getPokemon(idPokemon));
    }

    @GetMapping("/species/{idPokemon}/moves")
    public ResponseEntity<@NonNull List<MovementData>> getPokemonMoves(
        @PathVariable("idPokemon") @NonNull String idPokemon,
        @RequestParam(value = "prefix", required = false, defaultValue = "") @NonNull String prefix,
        @RequestParam(value = "type", required = false) @Nullable PokemonType type,
        @RequestParam(value = "moveClass", required = false) @Nullable MoveClass moveClass) {   //Tiene que tener mismo nombre que en la URL

        return ResponseEntity.ok(this.dexService.getPokemonMoves(idPokemon,prefix,type,moveClass));
    }

    @GetMapping("/moves")
    public ResponseEntity<@NonNull List<MovementData>> getMoves(
        @RequestParam(value = "prefix", required = false, defaultValue = "") @NonNull String prefix,
        @RequestParam(value = "type", required = false) @Nullable PokemonType type,
        @RequestParam(value = "moveClass", required = false) @Nullable MoveClass moveClass) { //special/physical/status

        return ResponseEntity.ok(this.dexService.searchMoves(prefix,type,moveClass));
    }

    @GetMapping("/moves/{idMove}")
    public ResponseEntity<@NonNull MovementData> getMove(
        @PathVariable("idMove") @NonNull String idMove) {

        return ResponseEntity.ok(this.dexService.getMove(idMove));
    }
}

