package com.vk.dwzkf.utils.http.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.dwzkf.utils.http.query.annotation.ValidatedQueryType;
import com.vk.dwzkf.utils.http.query.exception.ParsingException;
import com.vk.dwzkf.utils.http.query.exception.ParsingFormatException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RealQueryParser implements QueryParser {

    private final QueryEntityResolver resolver;
    private final Map<Class<? extends QueryParamValidator>, QueryParamValidator> validators = new ConcurrentHashMap<>();

    @Autowired
    public void registerValidators(List<QueryParamValidator> validatorsList) {
        validatorsList.forEach(v -> RealQueryParser.this.validators.put(v.type(), v));
    }

    @Override
    @SneakyThrows
    public RequestQuery parse(String query) {
        RequestQuery requestQuery = new RequestQuery();
        List<NameValuePair> pairs = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
        List<QueryEntity<?>> entityList = new LinkedList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (NameValuePair pair : pairs) {
            try {
                List<? extends QueryEntity<?>> entities = resolver.resolve(pair.getName(), pair.getValue());
                log.debug("Parsed: {}", entities);
                entityList.addAll(entities);
            } catch (ParsingException exception) {
                log.warn("Param cannot be parsed'{} : {}'. Added as simple url param. Exception message:{}",
                        pair.getName(),
                        pair.getValue(),
                        exception.getMessage());
                requestQuery.addSimpleParam(pair.getName(), pair.getValue());
            } catch (ParsingFormatException exception) {
                log.warn("Bad format. Error message: {}", exception.getMessage());
                throw new IllegalArgumentException(String.format("Bad format. " +
                                "Error message: %s.",
                        exception.getMessage()), exception);
            }
        }
        entityList.stream()
                .collect(Collectors.groupingBy(QueryEntity::getType))
                .forEach((type, queryEntities) -> requestQuery.addEntities(queryEntities));

        validateQueryParams(requestQuery.getEntities());
        log.info("Resolved query: Map:{}, List:{}", requestQuery.getSimpleParams(), requestQuery.getEntities());
        log.info("Query JSON: {}", objectMapper.writeValueAsString(requestQuery.getEntities()));
        return requestQuery;
    }

    //TODO: need to verify that always exists only two entities of type page with subtypes "offset" or "limit
    //TODO: example: [page[offset]=1,page[limit]=123]
    private void validateQueryParams(List<QueryEntity<?>> entityList) {
        entityList.stream()
                .filter(entity -> entity.getClass().isAnnotationPresent(ValidatedQueryType.class))
                .collect(Collectors.groupingBy(entity -> entity.getClass().getAnnotation(ValidatedQueryType.class).value()))
                .forEach(this::validateQueryParams);
    }

    private void validateQueryParams(Class<? extends QueryParamValidator> queryType, List<QueryEntity<?>> entities) {
        QueryParamValidator validator = validators.get(queryType);
        if (validator != null) {
            validator.validate(entities);
        }
    }
}
