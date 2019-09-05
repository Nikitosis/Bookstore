package com.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Action {
    @JsonProperty("take")
    TAKE,

    @JsonProperty("return")
    RETURN;
}
