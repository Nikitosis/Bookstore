package com.resources;
import com.api.Action;
import com.api.UserBookLog;
import com.dao.UserBookLogDao;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserBookLogResourceTest {

    private UserBookLogDao userBookLogDao =mock(UserBookLogDao.class);

    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new UserBookLogResource(userBookLogDao))
            .build();

    UserBookLog testLog;
    @Before
    public void init(){
        testLog=new UserBookLog();
        testLog.setAction(Action.TAKE);
        testLog.setDate(LocalDateTime.now());
        testLog.setBookId(12L);
        testLog.setUserId(13L);
        testLog.setId(31L);
    }

    @Test
    public void getLogsTest_byBookAndUser(){
        List<UserBookLog> testLogs= Arrays.asList(testLog);
        when(userBookLogDao.findByBookAndUser(eq(testLog.getUserId()),eq(testLog.getBookId()))).thenReturn(testLogs);

        List<UserBookLog> responseLogs=resources.target("/actions")
                                           .queryParam("userId",testLog.getUserId())
                                            .queryParam("bookId",testLog.getBookId())
                                            .request()
                                            .get(new GenericType<List<UserBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void getLogsTest_byBook(){
        List<UserBookLog> testLogs= Arrays.asList(testLog);
        when(userBookLogDao.findByBookId(eq(testLog.getBookId()))).thenReturn(testLogs);

        List<UserBookLog> responseLogs=resources.target("/actions")
                .queryParam("bookId",testLog.getBookId())
                .request()
                .get(new GenericType<List<UserBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void getLogsTest_byUser(){
        List<UserBookLog> testLogs= Arrays.asList(testLog);
        when(userBookLogDao.findByUser(eq(testLog.getUserId()))).thenReturn(testLogs);

        List<UserBookLog> responseLogs=resources.target("/actions")
                .queryParam("userId",testLog.getUserId())
                .request()
                .get(new GenericType<List<UserBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void getLogsTest_getAll(){
        List<UserBookLog> testLogs= Arrays.asList(testLog);
        when(userBookLogDao.findAll()).thenReturn(testLogs);

        List<UserBookLog> responseLogs=resources.target("/actions")
                .request()
                .get(new GenericType<List<UserBookLog>>(){});

        Assert.assertArrayEquals(testLogs.toArray(),responseLogs.toArray());
    }

    @Test
    public void createTest(){

        UserBookLog responseLog=resources.target("/actions")
                .request()
                .post(Entity.entity(testLog, MediaType.APPLICATION_JSON), UserBookLog.class);

        verify(userBookLogDao).save(eq(testLog));
        Assert.assertEquals(testLog,responseLog);
    }
}
