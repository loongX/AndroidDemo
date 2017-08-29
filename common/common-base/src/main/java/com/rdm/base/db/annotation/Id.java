
package com.rdm.base.db.annotation;

import com.rdm.base.annotation.PluginApi;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({
    FIELD
})
@Retention(RUNTIME)
@PluginApi(since = 4)
public @interface Id {
    /**
     * 设置主键名
     * 
     * @return
     */
    @PluginApi(since = 4)
    public String name() default "";

    /**
     * 字段默认值
     * 
     * @return
     */
    @PluginApi(since = 4)
    public String defaultValue() default "";
    @PluginApi(since = 4)
    public int strategy() default GenerationType.AUTO_INCREMENT;
}
