package com.softserveinc.logger.resources;

import com.softserveinc.logger.dao.BookDao;
import com.softserveinc.logger.dao.UserBookLogDao;
import com.softserveinc.logger.dao.UserBookPaymentLogDao;
import com.softserveinc.logger.models.BookStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Service
@Path("/statistics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsResource {
    private UserBookPaymentLogDao userBookPaymentLogDao;
    private UserBookLogDao userBookLogDao;
    private BookDao bookDao;

    @Autowired
    public StatisticsResource(UserBookPaymentLogDao userBookPaymentLogDao, UserBookLogDao userBookLogDao, BookDao bookDao) {
        this.userBookPaymentLogDao = userBookPaymentLogDao;
        this.userBookLogDao = userBookLogDao;
        this.bookDao = bookDao;
    }

    @GET
    @Path("/books/{bookId}")
    public Response getBookStatistics(@PathParam("bookId")Long bookId){
        BookStatistics bookStatistics=new BookStatistics();
        if(bookDao.findById(bookId)==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        bookStatistics.setTakenAmount(userBookLogDao.getBookTakenAmount(bookId));
        bookStatistics.setReturnedAmount(userBookLogDao.getBookReturnedAmount(bookId));
        bookStatistics.setTotalPayments(userBookPaymentLogDao.getBookTotalPayments(bookId));;
        if(bookStatistics.getTotalPayments() ==null)
            bookStatistics.setTotalPayments(new BigDecimal("0"));

        return Response.ok().entity(bookStatistics).build();
    }
}
