package com.vk.dwzkf.utils.http.query.annotation;


import com.vk.dwzkf.utils.http.query.QueryParamValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidatedQueryType {
    Class<? extends QueryParamValidator> value();
}
