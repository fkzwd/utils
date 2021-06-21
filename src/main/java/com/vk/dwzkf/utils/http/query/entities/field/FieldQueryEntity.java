package com.vk.dwzkf.utils.http.query.entities.field;


import com.vk.dwzkf.utils.http.query.QueryEntity;
import com.vk.dwzkf.utils.http.query.annotation.ValidatedQueryType;

@ValidatedQueryType(FieldQueryEntityValidator.class)
public class FieldQueryEntity extends QueryEntity<String> {
    public FieldQueryEntity(String type, String subtype, String value) {
        super(type, subtype, value);
    }

    public FieldQueryEntity() {
    }
}
