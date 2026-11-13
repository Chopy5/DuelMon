package gal.usc.etse.es.servicios.model.dto;

public record Sprite(String front, String back, String icon) {

    // Metodd para generar la URL de un sprite
    private static String url(String baseUrl, String kind, int number) {
        return baseUrl + "/" + kind + "/" + "%04d".formatted(number) + ".png";
    }

    // Constructor de sprites dada la url y numero -> en todo DTO que devuelva sprites se usan
    public static Sprite from(String baseUrl, int number) {

        return new Sprite(
                url(baseUrl, "front", number),
                url(baseUrl, "back", number),
                url(baseUrl, "icons", number)
        );
    }
}