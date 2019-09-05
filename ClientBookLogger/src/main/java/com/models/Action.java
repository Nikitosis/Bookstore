package com.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Action {
    @JsonProperty("take")
    TAKE,

    @JsonProperty("return")
    RETURN;
}
