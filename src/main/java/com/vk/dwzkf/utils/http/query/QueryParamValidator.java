package com.vk.dwzkf.utils.http.query;

import com.vk.dwzkf.utils.http.query.exception.QueryValidationException;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class QueryParamValidator {
    public abstract Class<? extends QueryParamValidator> type();

    public abstract void validate(List<QueryEntity<?>> entites) throws QueryValidationException;
}
