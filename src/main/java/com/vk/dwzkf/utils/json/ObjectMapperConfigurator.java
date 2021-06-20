package com.vk.dwzkf.utils.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.time.format.DateTimeFormatter.*;

public class ObjectMapperConfigurator {

    public static void configure(ObjectMapper bean) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDatetimeDeserializer());
        ParameterNamesModule parameterNamesModule = new ParameterNamesModule();

        bean.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        bean.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        bean.registerModule(parameterNamesModule);
        bean.registerModule(simpleModule);
    }

    @Slf4j
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        static final DateTimeFormatter DATE_FORMATTER_MAIN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        static final DateTimeFormatter DATE_FORMATTER_MEGA_MAIN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        static final List<DateTimeFormatter> formats = List.of(
                DATE_FORMATTER_MAIN,
                DATE_FORMATTER_MEGA_MAIN,
                DATE_FORMATTER,
                ISO_LOCAL_DATE,
                ISO_OFFSET_DATE,
                ISO_DATE,
                ISO_LOCAL_TIME,
                ISO_OFFSET_TIME,
                ISO_TIME,
                ISO_LOCAL_DATE_TIME,
                ISO_OFFSET_DATE_TIME,
                ISO_ZONED_DATE_TIME,
                ISO_DATE_TIME,
                ISO_ORDINAL_DATE,
                ISO_WEEK_DATE,
                ISO_INSTANT,
                BASIC_ISO_DATE,
                RFC_1123_DATE_TIME
        );

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            DateTimeParseException last = null;
            for (int i = 0; i < formats.size(); i++) {
                DateTimeFormatter format = formats.get(i);
                try {
                    String s = value.format(format);
                    gen.writeString(s);
                    return;
                } catch (DateTimeParseException e) {
                    if (last == null) {
                        last = e;
                    } else {
                        last.addSuppressed(e);
                    }
                }
            }
            if (last != null) {
                throw last;
            }
            throw new IllegalArgumentException("No date serializers found.");
        }
    }

    @Slf4j
    public static class LocalDatetimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
                throws IOException {
            String str = p.getText();
            DateTimeParseException last = null;
            for (int i = 0; i < LocalDateTimeSerializer.formats.size(); i++) {
                DateTimeFormatter format = LocalDateTimeSerializer.formats.get(i);
                try {
                    return LocalDateTime.parse(str, format);
                } catch (DateTimeParseException e) {
                    if (last == null) {
                        last = e;
                    } else {
                        last.addSuppressed(e);
                    }
                }
            }
            if (last != null) {
                throw last;
            }
            throw new IllegalArgumentException("No date deserializers found.");
        }
    }
}