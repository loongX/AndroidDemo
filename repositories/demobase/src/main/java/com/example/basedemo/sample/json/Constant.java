package com.example.basedemo.sample.json;

import com.example.basedemo.sample.json.bean.UserAddress;
import com.example.basedemo.sample.json.bean.UserSimpleOne;
import com.example.basedemo.sample.json.bean.UserSimpleTwo;

/**
 * Created by Administrator on 2017/6/27.
 */

public class Constant {

    //模拟数据1
    public static String userJsonOne =
    "{\"age\":26,\"email\":\"norman@futurestud.io\",\"isDeveloper\":true,\"name\":\"Norman\"," +
            "\"userAddress\":{\"city\":\"Magdeburg\",\"country\":\"Germany\",\"houseNumber\":\"42A\",\"street\":\"Main Street\"}}";

    public static UserAddress userAddress = new UserAddress()
            .withCity("Magdeburg")
            .withCountry("Germany")
            .withHouseNumber("42A")
            .withStreet("Main Street");

    public static UserSimpleOne userObjectOne = new UserSimpleOne()
            .withName("Norman")
            .withEmail("norman@futurestud.io")
            .withAge(26)
            .withIsDeveloper(true)
            .withUserAddress(userAddress);

    //模拟数据2
    public static String userJsonTwo = "{\"name\":\"Norman\",\"email\":\"norman@futurestud.io\",\"age\":26,\"isDeveloper\":true}";

    public static UserSimpleTwo userObject = new UserSimpleTwo(
        "Norman",
        "norman@futurestud.io",
        26,
        true
    );

}
