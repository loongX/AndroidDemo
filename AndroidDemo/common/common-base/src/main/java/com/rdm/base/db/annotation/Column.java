
package com.rdm.base.db.annotation;

import com.rdm.base.annotation.PluginApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
@PluginApi(since = 4)
public @interface Column {
    enum ConflictAction {
        ROLLBACK, ABORT, FAIL, IGNORE, REPLACE
    }

    /**
     * (Optional) The name of the column. Defaults to 
     * the property or field name.
     */
    @PluginApi(since = 4)
    String name() default "";

    @PluginApi(since = 4)
    int length() default -1;

    /**
     * (Optional) Whether the database column is nullable.
     */
    boolean nullable() default true;

    /**
     * (Optional) Whether the property is a unique key.  This is a 
     * shortcut for the UniqueConstraint annotation at the table 
     * level and is useful for when the unique key constraint is 
     * only a single field. This constraint applies in addition 
     * to any constraint entailed by primary key mapping and 
     * to constraints specified at the table level.
     */
    @PluginApi(since = 4)
    boolean unique() default false;

}
