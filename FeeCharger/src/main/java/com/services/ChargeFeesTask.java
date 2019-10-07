package com.services;

import com.MainConfig;
import com.crossapi.models.Book;
import com.crossapi.models.User;
import com.dao.BookDao;
import com.dao.FeeChargerDao;
import com.dao.UserDao;
import com.models.UserBook;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Future;

public class ChargeFeesTask extends TimerTask {
    private FeeChargerDao feeChargerDao;
    private UserDao userDao;
    private BookDao bookDao;
    private MainConfig mainConfig;
    private OktaService oktaService;

    public ChargeFeesTask(FeeChargerDao feeChargerDao, UserDao userDao, BookDao bookDao, MainConfig mainConfig, OktaService oktaService) {
        this.feeChargerDao = feeChargerDao;
        this.userDao = userDao;
        this.bookDao = bookDao;
        this.mainConfig = mainConfig;
        this.oktaService = oktaService;
    }

    public void run(){
        List<UserBook> expiredRents=feeChargerDao.getExpiredBookRent();
        for(UserBook expiredRent:expiredRents){
            if(canUserPayForRent(expiredRent)){
                extendBookRent(expiredRent);
            }
            else{
                sendReturnBook(expiredRent);
            }
        }
    }

    private boolean canUserPayForRent(UserBook rent){
        User user=userDao.findById(rent.getUserId());
        Book book=bookDao.findById(rent.getBookId());

        return user.getMoney()>=book.getDailyPrice();
    }

    private void extendBookRent(UserBook rent){
        Book book=bookDao.findById(rent.getBookId());
        feeChargerDao.chargeFee(rent.getUserId(),book.getDailyPrice());

        LocalDateTime paidUntil=rent.getPaidUntil()==null ? LocalDateTime.now() : rent.getPaidUntil();
        LocalDateTime payUntil=paidUntil.plusMinutes(mainConfig.getFeeChargeConfig().getRentPeriod());
        feeChargerDao.extendBookRent(rent.getUserId(),rent.getBookId(), payUntil);
    }

    private Future<Response> sendReturnBook(UserBook rent){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client= ClientBuilder.newClient();

        return client.target(mainConfig.getLibraryService().getUrl())
                .path("/users/"+rent.getUserId()+"/books/"+rent.getBookId())
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .delete();
    }
}
