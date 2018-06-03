
package com.rdm.base.annotation;

/**
 * 标明方法或者成员变量是需要暴露给插件的，凡是有此标记的地方不能轻易修改接口
 * 
 * @author
 */
public @interface PluginApi {

    int since() default 4;
    
}
