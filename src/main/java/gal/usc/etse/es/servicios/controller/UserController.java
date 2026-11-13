package gal.usc.etse.es.servicios.controller;

public class UserController {

    /*
    Usuarios:
    REGISTRO Y LOGIN (usamos verbos?? -> sí, es una excepción aceptada)
            -POST /api/v1/auth/register (crea los pokemon iniciales) (sino POST /users)
            -POST /api/v1/auth/login (sino POST /api/v1/auth/sessions)
        -POST /api/v1/auth/logout (sino DELETE /api/v1/auth/sessions/current)
    VER DATOS
        -GET /api/v1/users/me
        -PATCH /api/v1/users/me (cambiar datos)
        -GET /api/v1/users?name= (para buscar por nombre y así averiguar el id)
            -GET /api/v1/users/{userId} (ver datos reducidos de otros usuarios)


    Amigos:
    AMIGOS
        -GET    /api/v1/users/me/friends
        -PATCH /friend-requests/{id} status=accepted (acepta solicitud)
        -DELETE /api/v1/users/me/friends/{userId}

    SOLICITUDES:
            -POST   /api/v1/users/me/friend-requests {userId}
        -GET    /api/v1/users/me/friend-requests  direction in/out
        -DELETE /api/v1/users/me/friend-requests/{requestId} (rechaza o cancela, según quién lo llame)
        */

}
