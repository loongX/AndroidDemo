package com.example.ilog.log;




import com.example.ilog.log.appender.Appender;

import java.util.Enumeration;


public interface AppenderAttachable
{
	/**
	* 增加Appender
	*/
	public void addAppender(Appender newAppender);

	/**
	* 获取所有的Appenders
	*/
	public Enumeration<Appender> getAllAppenders();
  
	/**
	* 
	*/
	public boolean isAttached(Appender appender);	

	/**
	* 移除全部Appenders
	*/
	public void removeAllAppenders();
}

