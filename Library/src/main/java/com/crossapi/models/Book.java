package com.crossapi.models;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Book {
    private Long id;

    @NotNull
    @NotEmpty
    @Length(max = 255)
    private String name;

    private Boolean isTaken;

    public Boolean getTaken() {
        return isTaken;
    }

    public void setTaken(Boolean taken) {
        isTaken = taken;
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

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        Book book = (Book) obj;

        return Objects.equals(book.id,this.id) &&
                Objects.equals(book.name,this.name) &&
                Objects.equals(book.isTaken,this.isTaken);
    }
}
