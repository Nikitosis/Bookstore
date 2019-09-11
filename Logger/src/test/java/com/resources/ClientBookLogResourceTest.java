package com.resources;
import com.api.Action;
import com.api.ClientBookLog;
import com.dao.ClientBookLogDao;
import io.dropwizard.cli.Cli;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientBookLogResourceTest {

    private ClientBookLogDao clientBookLogDao=mock(ClientBookLogDao.class);

    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new ClientBookLogResource(clientBookLogDao))
            .build();

    ClientBookLog testLog;
    @Before
    public void init(){
        testLog=new ClientBookLog();
        testLog.setAction(Action.TAKE);
        testLog.setActionDate(LocalDateTime.now());
        testLog.setBookId(12L);
        testLog.setClientId(13L);
        testLog.setId(31L);
    }

    @Test
    public void getLogsTest_byBookAndClient(){
        List<ClientBookLog> testLogs= Arrays.asList(testLog);
        when(clientBookLogDao.findByBookAndClient(eq(testLog.getClientId()),eq(testLog.getBookId()))).thenReturn(testLogs);

        List<ClientBookLog> responseLogs=resources.target("/actions")
                                           .queryParam("clientId",testLog.getClientId())
                                            .queryParam("bookId",testLog.getBookId())
                                            .request()
                                            .get(new GenericType<List<ClientBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void getLogsTest_byBook(){
        List<ClientBookLog> testLogs= Arrays.asList(testLog);
        when(clientBookLogDao.findByBookId(eq(testLog.getBookId()))).thenReturn(testLogs);

        List<ClientBookLog> responseLogs=resources.target("/actions")
                .queryParam("bookId",testLog.getBookId())
                .request()
                .get(new GenericType<List<ClientBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void getLogsTest_byClient(){
        List<ClientBookLog> testLogs= Arrays.asList(testLog);
        when(clientBookLogDao.findByClientId(eq(testLog.getClientId()))).thenReturn(testLogs);

        List<ClientBookLog> responseLogs=resources.target("/actions")
                .queryParam("clientId",testLog.getClientId())
                .request()
                .get(new GenericType<List<ClientBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void getLogsTest_getAll(){
        List<ClientBookLog> testLogs= Arrays.asList(testLog);
        when(clientBookLogDao.findAll()).thenReturn(testLogs);

        List<ClientBookLog> responseLogs=resources.target("/actions")
                .request()
                .get(new GenericType<List<ClientBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void createTest(){

        ClientBookLog responseLog=resources.target("/actions")
                .request()
                .post(Entity.entity(testLog, MediaType.APPLICATION_JSON),ClientBookLog.class);

        verify(clientBookLogDao).save(eq(testLog));
        Assert.assertEquals(testLog,responseLog);
    }
}
