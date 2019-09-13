package com.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import java.util.Objects;

public class Book {

    @NotNull
    private Long id;

    @NotNull
    @Length(max = 255)
    private String name;

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

        return Objects.equals(book.name,this.name) &&
                Objects.equals(book.id,this.id);
    }
}
