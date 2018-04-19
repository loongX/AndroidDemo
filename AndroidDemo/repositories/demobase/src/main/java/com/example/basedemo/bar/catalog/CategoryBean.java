package com.example.basedemo.bar.catalog;

/**
 * Created by loongago on 2017-06-21.
 */

public class CategoryBean {

    private  Class[] CLAZZES;

    private String[] DESCRIBE;

    public Class[] getCLAZZES() {
        return CLAZZES;
    }

    public void setCLAZZES(Class[] CLAZZES) {
        this.CLAZZES = CLAZZES;
    }

    public String[] getDESCRIBE() {
        return DESCRIBE;
    }

    public void setDESCRIBE(String[] DESCRIBE) {
        this.DESCRIBE = DESCRIBE;
    }

    public CategoryBean(Class[] CLAZZES, String[] DESCRIBE) {
        this.CLAZZES = CLAZZES;
        this.DESCRIBE = DESCRIBE;
    }
}
