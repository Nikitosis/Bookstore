package com.services;

import com.MainConfig;
import com.configuration.FeeChargeConfig;
import com.dao.BookDao;
import com.dao.FeeChargerDao;
import com.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import javax.annotation.PostConstruct;
import java.util.Timer;

@Service
public class FeeChargerService {
    private BookDao bookDao;
    private FeeChargerDao feeChargerDao;
    private UserDao userDao;
    private MainConfig mainConfig;
    private OktaService oktaService;
    private Timer timer;

    @Autowired
    public FeeChargerService(BookDao bookDao, FeeChargerDao feeChargerDao, UserDao userDao, MainConfig mainConfig, OktaService oktaService) {
        this.bookDao = bookDao;
        this.feeChargerDao = feeChargerDao;
        this.userDao = userDao;
        this.mainConfig = mainConfig;
        this.oktaService = oktaService;
    }

    @PostConstruct
    public void startFeeCharging(){
        timer=new Timer();
        timer.schedule(
                new ChargeFeesTask(feeChargerDao,userDao,bookDao,mainConfig,oktaService),
                0,
                mainConfig.getFeeChargeConfig().getCheckExpirationInterval()*1000*60
        );
    }
}
