package gal.usc.etse.es.servicios.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (
    @NotBlank(message = "El nombre es obligatorio")
    String username,

    @NotBlank(message = "La contraseña es obligatoria")
    String password) {}
