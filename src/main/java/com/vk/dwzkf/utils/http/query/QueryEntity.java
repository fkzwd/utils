package com.vk.dwzkf.utils.http.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class QueryEntity<R> {
    protected String type;
    protected String subtype;
    protected R value;

    public String type() {
        return type;
    }

    public String subtype() {
        return subtype;
    }

    public R value() {
        return value;
    }
}
