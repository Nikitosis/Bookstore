package com.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;

public class Book {
    private Long id;

    @NotNull
    @Length(max = 255)
    private String name;

    private boolean isTaken;

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

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
