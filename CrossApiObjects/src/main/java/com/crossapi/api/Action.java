package com.crossapi.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Action {
    @JsonProperty("TAKE")
    TAKE,

    @JsonProperty("RETURN")
    RETURN;
}
