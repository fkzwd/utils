package com.vk.dwzkf.utils.http.query;

import com.vk.dwzkf.utils.http.query.exception.ParsingException;
import com.vk.dwzkf.utils.http.query.exception.ParsingFormatException;
import com.vk.dwzkf.utils.http.query.exception.UnsupportedFormatException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryEntityResolver {
    private static final String OPEN_BRACE_REGEX = "\\[";
    private static final String CLOSE_BRACE_REGEX = "]";
    private static final String OPEN_BRACE = "[";
    private static final String CLOSE_BRACE = "]";

    public List<? extends QueryEntity<?>> resolve(String name, String value) throws ParsingException, ParsingFormatException {
        String type = typeFromName(name);
        String subtype = subTypeFromName(name);
        QueryEntityType element = QueryEntityType.byType(type);
        try {
            element.validate(subtype, value);
        } catch (UnsupportedFormatException exception) {
            throw new ParsingException(exception.getMessage(), exception);
        }
        return element.resolve(subtype, value);
    }

    private String typeFromName(String name) {
        if (name.matches("[a-zA-Z0-9_\\-\\\\.]+" + OPEN_BRACE_REGEX + "[a-zA-Z0-9_\\-\\\\.]+" + CLOSE_BRACE_REGEX)) {
            return name.substring(0, name.indexOf(OPEN_BRACE));
        }
        return name;
    }

    private String subTypeFromName(String name) {
        if (name.matches("[a-zA-Z0-9_\\-\\\\.]+" + OPEN_BRACE_REGEX + "[a-zA-Z0-9_\\-\\\\.]+" + CLOSE_BRACE_REGEX)) {
            int start = name.indexOf(OPEN_BRACE) + 1;
            int end = name.lastIndexOf(CLOSE_BRACE);
            return name.substring(start, end);
        }
        return null;
    }
}
