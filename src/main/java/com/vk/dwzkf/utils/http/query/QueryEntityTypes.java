package com.vk.dwzkf.utils.http.query;

import com.vk.dwzkf.utils.http.query.exception.ParsingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class QueryEntityTypes {
    private final List<QueryEntityType> registeredTypes = new ArrayList<>();

    @Autowired
    public void registerTypes(List<QueryEntityType> types) {
        registeredTypes.addAll(types);
    }

    public QueryEntityType byTypeName(String typeName) throws ParsingException {
        return values().stream().filter(f -> f.getTypeName().equals(typeName))
                .findFirst()
                .orElseThrow(ParsingException::new);
    }

    public List<QueryEntityType> values() {
        return Collections.unmodifiableList(registeredTypes);
    }
}
