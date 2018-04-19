
package com.rdm.base.db.exception;

import com.rdm.base.annotation.PluginApi;

@PluginApi(since = 4)
public class DBException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @PluginApi(since = 4)
    public DBException() {
        super();
    }

    @PluginApi(since = 4)
    public DBException(String detailMessage) {
        super(detailMessage);
    }

    @PluginApi(since = 4)
    public DBException(Throwable throwable) {
        super(throwable);
    }

    public DBException(String detailMessage,Throwable throwable) {
        super(detailMessage,throwable);
    }

}
