package com.example.ilog.log.appender;


import com.example.ilog.log.LoggingEvent;
import com.example.ilog.log.layout.Layout;

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
