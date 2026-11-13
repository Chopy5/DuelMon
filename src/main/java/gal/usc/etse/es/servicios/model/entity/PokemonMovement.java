package gal.usc.etse.es.servicios.model.entity;

import gal.usc.etse.es.servicios.model.dto.MovementResponse;
import lombok.Getter;

public class PokemonMovement {
    // Warning de Lombok Getter??
    @Getter
    private MovementResponse movementData;
    private int remaining_pp;

    public PokemonMovement(MovementResponse movementData) {

        this.movementData = movementData;
        this.remaining_pp = movementData.pp();

    }

    public void validateMovement(){

    }

    public int getRemainingPP(){
        return this.remaining_pp;
    }

    public void setRemainingPP(int remainingPP){
        this.remaining_pp = remainingPP;
    }

    public void incrementPP(){
        this.remaining_pp++;
    }

    public void decrementPP(){
        this.remaining_pp--;
    }

    public void restorePP(){
        this.remaining_pp = this.movementData.pp();
    }

}
