package com.crossapi.testutils.mixins;

import com.crossapi.models.Book;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BookMixin extends Book {
    private Long id;

    @NotNull
    @NotEmpty
    @Length(max = 255)
    private String name;

    @Length(max=13)
    private String isbn;

    @Length(max=255)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String photoLink;

    //url can be set only programmatically, after uploading to storage
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String filePath;

    private BigDecimal price;
}
