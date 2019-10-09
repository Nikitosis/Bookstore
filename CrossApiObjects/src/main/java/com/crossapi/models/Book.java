package com.crossapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Book {
    private Long id;

    @NotNull
    @NotEmpty
    @Length(max = 255)
    private String name;

    @Length(max=13)
    private String isbn;

    @Length(max=255)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String photoLink;

    //url can be set only programmatically, after uploading to storage
    @JsonIgnore
    private String filePath;

    private BigDecimal price;

    public Book(){

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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
                Objects.equals(book.name,this.name)&&
                Objects.equals(book.isbn,this.isbn)&&
                Objects.equals(book.photoLink,this.photoLink) &&
                Objects.equals(book.filePath,this.filePath)&&
                Objects.equals(book.price,this.price);
    }
}
