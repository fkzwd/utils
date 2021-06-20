package com.vk.dwzkf.utils.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.vk.dwzkf.utils.json.ObjectMapperConfigurator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class RepositoryUtils {
    private static final ObjectMapper simpleObjectMapper = new ObjectMapper();
    private static final ObjectMapper snakeObjectMapper = new ObjectMapper();
    private final DSLContext dslContext;

    static {
        ObjectMapperConfigurator.configure(simpleObjectMapper);
        ObjectMapperConfigurator.configure(snakeObjectMapper);
        snakeObjectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }


    /**
     * Маппит из списка строн camelCase вида {fieldOne: 1} в список объектов с camelCase полями
     * @param routine - хранимка
     * @param objectKlass - класс для маппинга
     * @param args - аргументы хранимки
     * @param <T> - дженерик
     * @return - список
     */
    public <T> List<T> getList(String routine, Class<T> objectKlass, Object... args) {
        return getList(simpleObjectMapper, routine, objectKlass, args);
    }

    /**
     * Маппит из списка строк snake_case вида {field_one:1} в список объектов вида {fieldOne: 1},{fieldOne: 5}
     * @param routine - хранимка
     * @param objectKlass - класс для маппинга
     * @param args - аргументы хранимки
     * @param <T> - дженерик
     * @return - список
     */
    public <T> List<T> getListSnake(String routine, Class<T> objectKlass, Object... args) {
        return getList(snakeObjectMapper, routine, objectKlass, args);
    }

    /**
     * @param routine     sql хранимки, например schema.getsomething(?,?,?)
     * @param objectKlass класс, в который будет сериализован setOf(json)
     * @param args        аргументы для хранимки
     * @param <T>         - generic param
     * @return List {@param <T>} -- лист с обьектами данного класса
     */
    public <T> List<T> getList(ObjectMapper mapper, String routine, Class<T> objectKlass, Object... args) {
        return dslContext.selectFrom(routine, args)
                .fetchInto(String.class)
                .stream()
                .map(o -> mapTo(mapper, o, objectKlass))
                .collect(Collectors.toList());
    }

    /**
     * If db-function returns simple json (not setof) like "[{},{}]"
     * use this function to map it into List<T>
     *
     * @param routine     - sql хранимка
     * @param tArrayKlass - array klass to map
     * @param args        - sql args
     * @param <T>         - klass
     * @return - List
     */
    public <T> List<T> getArray(String routine, Class<T[]> tArrayKlass, Object... args) {
        return getArray(simpleObjectMapper, routine, tArrayKlass, args);
    }

    /**
     * Маппит массив объектов snake_case [{},{}] в список объектов camelCase
     * @param routine - хранимка
     * @param tArrayKlass - Класс[] массива в который будет мапить
     * @param args - аргументы хранимки
     * @param <T> - generic param
     * @return - List
     */
    public <T> List<T> getArraySnake(String routine, Class<T[]> tArrayKlass, Object... args) {
        return getArray(snakeObjectMapper, routine, tArrayKlass, args);
    }

    public <T> List<T> getArray(ObjectMapper mapper, String routine, Class<T[]> tArrayKlass, Object... args) {
        return Arrays.asList(mapTo(mapper, dslContext.selectFrom(routine, args)
                .fetchOneInto(String.class), tArrayKlass));
    }

    /**
     * @param routine     sql хранимки, например 'schema.getsomething(?,?,?)'
     * @param objectKlass класс, в который будет сериализован json от хранимки
     * @param args        - аргументы хранимки
     * @param <T>         - generic param
     * @return - объект заданного класса
     */
    public <T> T getObject(String routine, Class<T> objectKlass, Object... args) {
        return mapTo(dslContext.selectFrom(routine, args)
                .fetchOneInto(String.class), objectKlass);
    }

    /**
     * @param routine     sql хранимки, например 'schema.getsomething(?,?,?)'
     * @param objectKlass класс, в который будет сериализован json от хранимки
     * @param args        - аргументы хранимки
     * @param <T>         - generic param
     * @return - объект заданного класса
     */
    public <T> T getObjectFromSnake(String routine, Class<T> objectKlass, Object... args) {
        return mapFromSnake(dslContext.selectFrom(routine, args)
                .fetchOneInto(String.class), objectKlass);
    }

    /**
     * Just execute sql and do nothing
     *
     * @param routine - routine
     * @param args    - args of routine
     */
    public void execute(String routine, Object... args) {
        dslContext.selectFrom(routine, args).execute();
    }

    /**
     * @param json json String
     * @param kl   - класс в который будет десериализовано
     * @param <T>  - generic param
     * @return - object of {@param <T>} type or null if json is null
     */
    public <T> T mapTo(String json, Class<T> kl) {
        return mapTo(simpleObjectMapper, json, kl);
    }

    public <T> T mapFromSnake(String json, Class<T> kl) {
        return mapTo(snakeObjectMapper, json, kl);
    }

    public static <T> T mapTo(ObjectMapper mapper, String json, Class<T> kl) {
        try {
            if (json == null) {
                return null;
            }
            return mapper.readValue(json, kl);
        } catch (Exception exception) {
            log.error("Exception occurred. {}", exception.getMessage(), exception);
            throw new IllegalArgumentException(String.format("Cannot read json[%s] to [%s]", json, kl.toString()), exception);
        }
    }

    public static String createRoutine(String schema, String routineName, ParamType... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(schema);
        sb.append(".");
        sb.append(routineName);
        sb.append("(");
        if (params != null && params.length > 0) {
            for (ParamType param : params) {
                sb.append(param.asArgument);
                sb.append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }

    public static String createRoutine(String schema, String routineName, int paramCount) {
        StringBuilder sb = new StringBuilder();
        sb.append(schema);
        sb.append(".");
        sb.append(routineName);
        sb.append("(");
        if (paramCount > 0) {
            for (int i = 0; i < paramCount; i++) {
                sb.append(ParamType.DEFAULT.asArgument);
                sb.append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }
}
