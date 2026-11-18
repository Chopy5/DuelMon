package gal.usc.etse.es.servicios.model.dto;

public record Cry(String base) {

    // Metodd para generar la URL de un sprite
    private static String buildUrl(String baseUrl, int number) {
        return baseUrl + "/" + number + ".ogg";
    }

    // Constructor de sprites dada la url y numero -> en todo DTO que devuelva sprites se usan
    public static Cry from(String baseUrl, int number) {

        return new Cry(buildUrl(baseUrl, number));
    }
}
