package com.softserveinc.cross_api_objects.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Action {
    @JsonProperty("TAKE")
    TAKE,

    @JsonProperty("RETURN")
    RETURN;
}
