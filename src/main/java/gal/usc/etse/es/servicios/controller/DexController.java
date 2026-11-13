package gal.usc.etse.es.servicios.controller;
import gal.usc.etse.es.servicios.model.dto.PokemonResponse;
import gal.usc.etse.es.servicios.model.enums.PokemonType;
import gal.usc.etse.es.servicios.model.dto.MovementResponse;
import gal.usc.etse.es.servicios.model.enums.MoveClass;
import gal.usc.etse.es.servicios.service.DexService;
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
    //Al arrancar, Spring hace una pasada por todo el codigo comprobando las anotaciones, generando un registro de definiciones
    //En esta caso, la anotacion @Service sigue el patron Singleton
    //entonces Spring la instanciara y se la pasara a los constructores que la necesiten y usen @Autowired


    // NO TENEMOS DEFAULTVALUE "" en los prefijos porque los nulls los gestiona el servicio

    @Autowired //No se necesita si hay un solo constructor pero bueno
    public DexController(DexService dexService) {
        this.dexService = dexService;
    }

    @GetMapping("/species")  //GetMapping es para rutas de endpoints concretos, en este caso GET
    public ResponseEntity<List<PokemonResponse>> getPokemons(   //Devuelve una list de pokemon, nunca puede ser null -> CASI SIEMPRE
         @RequestParam(value = "prefix", required = false) @Nullable String prefix,
         @RequestParam(value = "type", required = false) @Nullable PokemonType type) { // parametro opcional de tipo del query, String

        return ResponseEntity.ok(this.dexService.searchPokemon(prefix,type));
    }

    @GetMapping("/species/{pokemonId}")
    public ResponseEntity<PokemonResponse> getPokemon(
        @PathVariable("pokemonId") String pokemonId) {

        return ResponseEntity.ok(this.dexService.getPokemon(pokemonId));
    }

    @GetMapping("/species/{pokemonId}/moves")
    public ResponseEntity<List<MovementResponse>> getPokemonMoves(
        @PathVariable("pokemonId") String pokemonId,    // No hace falta poner required = true porque es parte de la ruta, si no está presente ya da 404
        @RequestParam(value = "prefix", required = false) @Nullable String prefix,
        @RequestParam(value = "type", required = false) @Nullable PokemonType type,
        @RequestParam(value = "category", required = false) @Nullable MoveClass category) {

        return ResponseEntity.ok(this.dexService.searchPokemonMoves(pokemonId,prefix,type,category));
    }

    @GetMapping("/moves")
    public ResponseEntity<List<MovementResponse>> getMoves(
        @RequestParam(value = "prefix", required = false) @Nullable String prefix,
        @RequestParam(value = "type", required = false) @Nullable PokemonType type,
        @RequestParam(value = "category", required = false) @Nullable MoveClass category) { //special/physical/status

        return ResponseEntity.ok(this.dexService.searchMoves(prefix,type,category));
    }

    @GetMapping("/moves/{moveId}")
    public ResponseEntity<MovementResponse> getMove(
        @PathVariable("moveId") String moveId) {

        return ResponseEntity.ok(this.dexService.getMove(moveId));
    }
}
