package gal.usc.etse.es.servicios.config;

import gal.usc.etse.es.servicios.utils.FormatUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.ValueSerializerModifier;

/*
    * Configuración de Jackson para la serialización y deserialización de enums como slug
    * Definimos un configuration que se escanea al inicio en Spring
    * Los modulos los declaramos como beans para que se instacien a inicio y luego los gestione Spring
    * Cuando Spring crea el ObjectMapper, registra todos los módulos que encuentre -> se registra nuestro módulo
    * Creamos un modificador ValueSerializerModifier para modificar el comportamiento de ciertos tipos de datos (en este caso, los enums)
    * Creamos un serializador y deserializador personalizados y que se llaman cuando la clase es del paquete de la aplicación
 */


@Configuration
public class JacksonConfig {

    // Para que solo se aplique sobre los enums de nuestros paquetes
    private static final String BASE_PACKAGE = "gal.usc.etse.es.servicios.";

    // Definimos un módulo con serializadores personalizados -> Spring lo registra en el ObjectMapper
    @Bean
    public SimpleModule slugEnumModule() {
        SimpleModule module = new SimpleModule();

        // Añadimos un serializador para convertir los enums de la aplicación a slug.
        module.setSerializerModifier(new ValueSerializerModifier() {
            @Override
            public ValueSerializer<?> modifyEnumSerializer(
                    SerializationConfig config,
                    JavaType type,
                    BeanDescription.Supplier description,
                    ValueSerializer<?> defaultSerializer) {
                if (type.getRawClass().getPackageName().startsWith(BASE_PACKAGE)) {
                    return new SlugEnumSerializer();
                }

                return defaultSerializer;
            }
        });

        // Añadimos un deserializador para aceptar enums escritos como slug
        module.setDeserializerModifier(new ValueDeserializerModifier() {
            @Override
            public ValueDeserializer<?> modifyEnumDeserializer(
                    DeserializationConfig config,
                    JavaType type,
                    BeanDescription.Supplier description,
                    ValueDeserializer<?> defaultDeserializer) {
                // Obtiene la clase enum que Jackson está deserializando
                Class<?> enumClass = type.getRawClass();

                // Si el enum es de la aplicación aplica nuestro deserializador
                if (enumClass.getPackageName().startsWith(BASE_PACKAGE)) {
                    return new SlugEnumDeserializer(enumClass);
                }

                // Si no mantiene el default
                return defaultDeserializer;
            }
        });

        return module;
    }

    // Serializador para convertir el enum a slug
    private static final class SlugEnumSerializer extends ValueSerializer<Enum<?>> {
        @Override
        public void serialize(Enum<?> value, JsonGenerator generator, SerializationContext context)
                throws JacksonException {
            generator.writeString(FormatUtils.toSlug(value.name()));    // Simplemente escribe el nombre del enum como slug
        }
    }

    // Deserializador para convertir el slug recibido al enum
    private static final class SlugEnumDeserializer extends ValueDeserializer<Object> {
        private final Class<?> enumClass;

        private SlugEnumDeserializer(Class<?> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})    // Quitamos warnings, sabemos que la clase es enum seguro
        public Object deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            String value = parser.getString();
            // Si el texto está vacío saca null
            if (value.isBlank()) {
                return null;
            }

            // Normaliza y convierte el valor usando la utilidad común.
            return FormatUtils.toEnum((Class) enumClass, value);
        }
    }
}
