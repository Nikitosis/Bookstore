package com.softserveinc.library.resources;

import com.softserveinc.cross_api_objects.models.Country;
import com.softserveinc.library.dao.CountryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
@Path("/countries")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CountryResource {
    private CountryDao countryDao;

    @Autowired
    public CountryResource(CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @GET
    public Response getCountries(){
        return Response.ok(countryDao.findAll()).build();
    }
}
