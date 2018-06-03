
package com.rdm.base.db.annotation;

import com.rdm.base.annotation.PluginApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the primary table for the annotated entity. Additional tables may be
 * specified using {@link SecondaryTable} or {@link SecondaryTables} annotation.
 * <p>
 * If no <code>Table</code> annotation is specified for an entity class, the default values apply.
 * 
 * <pre>
 *    Example:
 * 
 *    &#064;Entity
 *    &#064;Table(name="CUST", version=2)
 *    public class Customer { ... }
 * </pre>
 * 
 * @author hugozhong
 * @date 2013-11-15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@PluginApi(since = 4)
public @interface Table {
    /**
     * (Optional) The name of the table.
     * <p>
     * Defaults to the entity name.
     */
    @PluginApi(since = 4)
    String name() default "";

    /**
     * The version of the table.
     */
    @PluginApi(since = 4)
    int version();

    @PluginApi(since = 10)
    boolean dynamicClass() default false;
}
