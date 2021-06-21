package com.vk.dwzkf.utils.http.query;

import com.vk.dwzkf.utils.http.query.exception.ParsingException;
import com.vk.dwzkf.utils.http.query.exception.ParsingFormatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@RequiredArgsConstructor
public abstract class QueryEntityType {
    private final String typeName;

    public abstract List<? extends QueryEntity<?>> resolve(String subtype, String value) throws ParsingException;

    public abstract void validate(String subtype, String value) throws ParsingFormatException;
}
