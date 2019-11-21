package com.softserveinc.cross_api_objects.dao;

import com.softserveinc.cross_api_objects.models.Country;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CountryDao {

    @Select("SELECT * FROM countries")
    List<Country> findAll();

    @Select("SELECT * FROM countries WHERE name=#{name}")
    Country findByName(@Param("name") String name);
}
