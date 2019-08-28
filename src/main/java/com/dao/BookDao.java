package com.dao;

import com.models.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BookDao {
    @Select("SELECT * FROM books")
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE id=#{id}")
    Book findById(Long id);

    @Insert("INSERT INTO books (name) VALUES(#{name})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void save(Book client);

    @Update("UPDATE books SET name=#{name} WHERE id=#{id}")
    void update(Book client);

    @Delete("DELETE FROM books WHERE id=#{id}")
    void delete(Long id);
}
