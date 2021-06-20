package com.vk.dwzkf.utils.jooq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ParamType {
    DEFAULT("?"),
    BIGINT("?"),
    JSON("cast(? as json)"),
    INTEGER("cast(? as integer)"),
    NUMERIC("cast(? as numeric)"),
    TEXT("cast(? as text)"),
    TIMESTAMP("cast(? as timestamp)");

    public final String asArgument;
}