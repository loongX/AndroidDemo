package com.pxl.base.log.appender;

import com.pxl.base.log.LoggingEvent;
import com.pxl.base.log.layout.Layout;

/**
 * Log输出位置
 */
public interface Appender
{
	public void close();
  
	/**
	 * 输出LoggingEvent
	 */
	public void doAppend(LoggingEvent event);


	/**
	 * 设置{@link Layout}
	 */
	public void setLayout(Layout layout);
}
