package com.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class Role {
    @NotNull
    @Length(max = 255)
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
