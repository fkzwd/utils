package com.vk.dwzkf.utils.http.query;

import com.vk.dwzkf.utils.http.query.entities.field.FieldQueryEntity;
import com.vk.dwzkf.utils.http.query.exception.ParsingException;
import com.vk.dwzkf.utils.http.query.exception.ParsingFormatException;
import com.vk.dwzkf.utils.http.query.exception.UnsupportedFormatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.vk.dwzkf.utils.CodeUtil.equalsAny;
import static com.vk.dwzkf.utils.CodeUtil.not;
import static com.vk.dwzkf.utils.http.query.QueryUtils.*;


@Getter
@RequiredArgsConstructor
public enum QueryEntityType {
    FIELD("field") {
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
    };

    private final String typeName;

    public abstract List<? extends QueryEntity<?>> resolve(String subtype, String value) throws ParsingException;

    public abstract void validate(String subtype, String value) throws ParsingFormatException;

    public static QueryEntityType byType(String type) throws ParsingException {
        return Arrays.stream(values()).filter(f -> f.typeName.equals(type))
                .findFirst()
                .orElseThrow(ParsingException::new);
    }
}
