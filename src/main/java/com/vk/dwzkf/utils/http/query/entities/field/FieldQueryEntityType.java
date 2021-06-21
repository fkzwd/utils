package com.vk.dwzkf.utils.http.query.entities.field;

import com.vk.dwzkf.utils.http.query.QueryEntity;
import com.vk.dwzkf.utils.http.query.QueryEntityType;
import com.vk.dwzkf.utils.http.query.exception.ParsingFormatException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FieldQueryEntityType extends QueryEntityType {
    public FieldQueryEntityType() {
        super("field");
    }

    @Override
    public List<? extends QueryEntity<?>> resolve(String subtype, String value) {
        List<QueryEntity<String>> list = new ArrayList<>();
        list.add(new FieldQueryEntity(getTypeName(), subtype, value));
        return list;
    }

    @Override
    public void validate(String subtype, String value) throws ParsingFormatException {
        if (subtype == null) {
            throw new ParsingFormatException("Unexpected subtype for typeParameter: " + getTypeName());
        }
    }
}
