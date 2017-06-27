package com.example.basedemo.Json;

import com.example.basedemo.sample.json.Constant;
import com.example.basedemo.sample.json.bean.UserSimpleOne;
import com.example.basedemo.sample.json.bean.UserSimpleTwo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/6/26.
 */
public class GsonUtilTest {

    enum testConstant{testUserSimpleOne,testUserSimpleTwo};

    testConstant tesType;

    @Before
    public void setUp() throws Exception {
        tesType = testConstant.testUserSimpleOne;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void gsonString() throws Exception {
        switch (tesType) {
            case testUserSimpleOne:
                testUserSampleOneGsonString();
                break;
            case testUserSimpleTwo:
                testUserSampleTwoGsonString();
                break;
        }
    }

    private void testUserSampleOneGsonString() {
        String gsonString = GsonUtil.GsonString(Constant.userObjectOne);
        assertEquals(Constant.userJsonOne, gsonString);
    }

    private void testUserSampleTwoGsonString() {
        String gsonString = GsonUtil.GsonString(Constant.userObject);
        assertEquals(Constant.userJsonTwo, gsonString);
    }

    @Test
    public void gsonToBean() throws Exception {
        switch (tesType){
            case testUserSimpleOne:
                testUserSampleOneGsonToBean();
                break;
            case testUserSimpleTwo:
                testUserSampleTwoGsonToBean();
                break;
        }

    }

    private void testUserSampleOneGsonToBean() {
//        String userJson = "{\"name\":\"Norman\",\"email\":\"norman@futurestud.io\",\"age\":26,\"isDeveloper\":true}";
        UserSimpleOne userObjec1 = GsonUtil.GsonToBean(Constant.userJsonOne, UserSimpleOne.class);
        assertEquals(Constant.userObjectOne.getName(), userObjec1.getName());
        assertEquals(Constant.userObjectOne.getEmail(), userObjec1.getEmail());
        assertEquals(Constant.userObjectOne.getAge(), userObjec1.getAge(), 0);
        assertEquals(Constant.userObjectOne.getIsDeveloper(), userObjec1.getIsDeveloper());
        assertEquals(Constant.userObjectOne.getUserAddress().getCity(), userObjec1.getUserAddress().getCity());
        assertEquals(Constant.userObjectOne.getUserAddress().getCountry(), userObjec1.getUserAddress().getCountry());
        assertEquals(Constant.userObjectOne.getUserAddress().getHouseNumber(), userObjec1.getUserAddress().getHouseNumber());
        assertEquals(Constant.userObjectOne.getUserAddress().getStreet(), userObjec1.getUserAddress().getStreet());
    }

    private void testUserSampleTwoGsonToBean() {
        String userJson = "{\"name\":\"Norman\",\"email\":\"norman@futurestud.io\",\"age\":26,\"isDeveloper\":true}";
        UserSimpleTwo userObjec1 = GsonUtil.GsonToBean(userJson, UserSimpleTwo.class);
        assertEquals(Constant.userObject.getName(), userObjec1.getName());
        assertEquals(Constant.userObject.getEmail(), userObjec1.getEmail());
        assertEquals(Constant.userObject.getAge(), userObjec1.getAge());
        assertEquals(Constant.userObject.isDeveloper(), userObjec1.isDeveloper());
    }

    @Test
    public void gsonToList() throws Exception {

    }

    @Test
    public void jsonToList() throws Exception {

    }

    @Test
    public void gsonToListMaps() throws Exception {

    }

    @Test
    public void gsonToMaps() throws Exception {

    }

}