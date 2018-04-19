package com.example.basedemo.ilog.log.appender;


import com.example.basedemo.ilog.log.LoggingEvent;
import com.example.basedemo.ilog.log.layout.Layout;

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
