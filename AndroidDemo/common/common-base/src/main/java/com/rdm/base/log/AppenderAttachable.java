package com.rdm.base.log;


import com.rdm.base.log.appender.Appender;

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

