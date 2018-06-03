
package com.rdm.base.db.annotation;

import com.rdm.base.annotation.PluginApi;

@PluginApi(since = 4)
public interface GenerationType {
    @PluginApi(since = 4)
    public static final int ASSIGN = 1; 
    @PluginApi(since = 4)
    public static final int UUID = 2;
    @PluginApi(since = 4)
    public static final int  AUTO_INCREMENT = 3;
}
