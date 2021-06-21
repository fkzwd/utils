package com.vk.dwzkf.utils.http.query.entities.field;

import com.vk.dwzkf.utils.http.query.QueryEntity;
import com.vk.dwzkf.utils.http.query.QueryParamValidator;
import com.vk.dwzkf.utils.http.query.exception.QueryValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FieldQueryEntityValidator extends QueryParamValidator {
    @Override
    public Class<? extends QueryParamValidator> type() {
        return FieldQueryEntityValidator.class;
    }

    @Override
    public void validate(List<QueryEntity<?>> entites) throws QueryValidationException {

    }
}
