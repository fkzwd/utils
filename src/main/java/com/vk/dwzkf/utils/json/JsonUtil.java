package com.vk.dwzkf.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class JsonUtil {
    private static final ObjectMapper snakeCaseObjMapper = new ObjectMapper();
    private static final ObjectMapper simpleObjectMapper = new ObjectMapper();
    private static final Map<Class<?>, Class<?>> classToArrayClass = new ConcurrentHashMap<>();

    static {
        ObjectMapperConfigurator.configure(simpleObjectMapper);
        ObjectMapperConfigurator.configure(snakeCaseObjMapper);
        snakeCaseObjMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T[]> getArrayType(Class<T> tClass) {
        Class<?> cl = classToArrayClass.get(tClass);
        if (cl == null) {
            Class<T[]> tArrayClass = (Class<T[]>) Array.newInstance(tClass, 0).getClass();
            classToArrayClass.put(tClass, tArrayClass);
            return tArrayClass;
        }
        return (Class<T[]>) cl;
    }

    /**
     * Converts string value of object with camelCase fields
     * @param json -- String value of some object with camelCase fields
     * @param t -- class To map
     * @param <T> -- ...
     * @return -- T object
     */
    public static <T> T convert(String json, Class<T> t) {
        return convert(simpleObjectMapper, json, t);
    }

    private static <T> T convert(ObjectMapper objectMapper, String json, Class<T> t) {
        try {
            return objectMapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing Json'{}' to Class'{}'", json, t, e);
            throw new IllegalArgumentException("Json could not parsed: " + json);
        }
    }

    public static String writeAsString(Object o, boolean isSimpleMapper) {
        try {
            return (isSimpleMapper ? simpleObjectMapper.writeValueAsString(o) : snakeCaseObjMapper.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal argument:" + o);
        }
    }

    public static <T, R> void convertUnknownToMap(String json, Map<T, R> map) {
        try {
            Map<T, R> readedValues = snakeCaseObjMapper.readValue(json, new TypeReference<>() {
            });
            map.putAll(readedValues);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing Json'{}' to Class'{}'", json, map, e);
            throw new IllegalArgumentException("Json could not parsed: " + json);
        }
    }


    /**
     *
     * @param json json of array of some object with camelCase fields; [{"fieldOne":1}]
     * @param tClass - class of T to map to List<T>
     * @param <T> - ...
     * @return - List<T>
     */
    public static <T> List<T> toList(String json, Class<T> tClass) {
        return toList(simpleObjectMapper, json, tClass);
    }

    /**
     *
     * @param json String of array of some object with fields with snake_case naming: [{"field_one":1}]
     * @param tClass class with camelCase fields like 'private Integer fieldOne'
     * @param <T> - ...
     * @return - List<T> with camelCase fields
     */
    public static <T> List<T> listFromSnake(String json, Class<T> tClass) {
        return toList(snakeCaseObjMapper, json, tClass);
    }

    public static <T> List<T> toList(ObjectMapper mapper, String json, Class<T> tClass) {
        Class<T[]> arrayType = getArrayType(tClass);
        try {
            return Arrays.asList(mapper.readValue(json, arrayType));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing Json'{}' to Class'{}'", json, arrayType, e);
            throw new IllegalArgumentException("Json could not parsed: " + json);
        }
    }

    public static String json(ObjectMapper mapper, Object o) throws JsonProcessingException {
        String result = mapper.writeValueAsString(o);
        if (result.equalsIgnoreCase("null")) {
            return null;
        }
        return result;
    }

    /**
     * Write json String of object from camelCase to camelCase
     * @param o - object
     * @return - String
     * @throws JsonProcessingException if error
     */
    public static String json(Object o) throws JsonProcessingException {
        return json(simpleObjectMapper, o);
    }

    /**
     * Write json String of object from camelCase to snake_case
     * @param o - Object
     * @return - String
     * @throws JsonProcessingException - if error
     */
    public static String snakeJson(Object o) throws JsonProcessingException {
        return json(snakeCaseObjMapper, o);
    }

    /**
     * Map from String snake_case to Object with camelCase fields; from {"field_one":1} to Object{fieldOne: 1}
     * @param s - String
     * @param tClass - Class to map
     * @param <T> - Generic class
     * @return - Object of T type
     */
    public static <T> T convertFromSnake(String s, Class<T> tClass) {
        return convert(snakeCaseObjMapper, s, tClass);
    }

    /**
     * Write object from camelCase to camelCase or throw RuntimeException
     * @param o - object
     * @return - String
     */
    public static String jsonOrNull(Object o) {
        try {
            return json(simpleObjectMapper, o);
        } catch (JsonProcessingException exception) {
            log.error("Exception while write object as string. {}", exception.getMessage(), exception);
            throw new JsonParserException("Cannot parse object" +
                    o.toString() + " to json.", exception);
        }
    }

    /**
     * Write object from camelCase to snake_case json or throw RuntimeException
     * @param o - object
     * @return - String
     */
    public static String snakeJsonOrNull(Object o) {
        try {
            return json(snakeCaseObjMapper, o);
        } catch (JsonProcessingException exception) {
            log.error("Exception while write object as string. {}", exception.getMessage(), exception);
            throw new JsonParserException("Cannot parse object" +
                    o.toString() + " to json.", exception);
        }
    }

    @SuppressWarnings("rawtypes")
    public static String json(Supplier s) throws JsonProcessingException {
        String result = simpleObjectMapper.writeValueAsString(s.get());
        if (result.equalsIgnoreCase("null")) {
            return null;
        }
        return result;
    }
}