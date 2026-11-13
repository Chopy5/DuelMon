package gal.usc.etse.es.servicios.config;

import gal.usc.etse.es.servicios.utils.FormatUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
    * Configuración de Spring MVC para la conversión de Strings a Enums
    * Definimos un configuration que se escanea al inicio en Spring
    * Spring configura MVC cogiendo todos los beans de tipo WebMvcConfigurer -> coge este
    * Añadimos una fabrica de conversores para convertir Strings a Enums
    * Spring MVC llama a la fabrica para obtener un conversor concreto para cada tipo de Enum cuando realice la conversion
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Metodo que llama Spring para registrar los conversores
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }

    // Fabrica de conversores para convertir Strings a Enums
    private static class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

        // Metodo que devuelve el conversor
        @Override
        public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
            return new StringToEnumConverter<>(targetType);
        }

        // Clase conversor concreto
        private static class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

            private final Class<T> enumType;

            public StringToEnumConverter(Class<T> enumType) {
                this.enumType = enumType;
            }

            // Convierte un String el enum del tipo
            @Override
            public T convert(String source) {
                // Si el parámetro viene vacío lo tratamos como nulo
                if (source == null || source.isBlank()) {
                    return null;
                }

                return FormatUtils.toEnum(enumType, source);
            }
        }
    }
}