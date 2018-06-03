package com.slzr.dagger2test.app.act;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by pxl on 2017/9/14.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ForActivity {
}
