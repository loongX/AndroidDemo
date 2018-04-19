package com.slzr.dagger2test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


import javax.inject.Qualifier;

/**
 * Created by pxl on 2017/9/14.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface SelfType {
    int value() default 1;
}
