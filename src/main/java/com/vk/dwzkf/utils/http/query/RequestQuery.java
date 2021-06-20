package com.vk.dwzkf.utils.http.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
@Slf4j
public class RequestQuery {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<String>> simpleParams = new ConcurrentHashMap<>();
    private final List<QueryEntity<?>> entities = Collections.synchronizedList(new LinkedList<>());

    public void addEntity(QueryEntity<?> entity) {
        entities.add(entity);
    }

    public void addEntities(List<QueryEntity<?>> entities) {
        this.entities.addAll(entities);
    }

    public void addSimpleParam(String paramName, String paramValue) {
        List<String> values = simpleParams.get(paramName);
        if (values == null) {
            simpleParams.putIfAbsent(paramName, Collections.synchronizedList(new LinkedList<>()));
            values = simpleParams.get(paramName);
        }
        values.add(paramValue);
    }

    public String getSingleParam(String name) {
        List<String> params4name = getParams(name);
        return params4name == null ? null : params4name.get(0);
    }

    public List<String> getParams(String name) {
        return simpleParams.get(name);
    }

    public String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(entities);
    }

    public String toJsonOrException() {
        try {
            return toJson();
        } catch (JsonProcessingException exception) {
            log.error("Exception occurred. {}", exception.getMessage(), exception);
            throw new RuntimeException("Exception while try to write entities as string.", exception);
        }
    }
}
