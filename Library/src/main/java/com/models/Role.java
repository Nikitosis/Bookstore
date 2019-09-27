package com.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class Role {
    private Long id;

    @NotNull
    @Length(max = 255)
    private String name;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
