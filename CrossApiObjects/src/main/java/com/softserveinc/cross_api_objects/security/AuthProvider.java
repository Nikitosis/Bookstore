package com.softserveinc.cross_api_objects.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AuthProvider {
    @JsonProperty("local")
    local,
    @JsonProperty("google")
    google,
    @JsonProperty("facebook")
    facebook
}
