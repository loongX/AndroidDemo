package com.rdm.base.log.layout;

import com.rdm.base.log.LoggingEvent;
import com.rdm.base.log.layout.Layout;


public class LogCatLayout extends Layout 
{
	StringBuffer sbuf = new StringBuffer(256);

	public LogCatLayout() 
	{

	}

	public String format(LoggingEvent event) 
	{
		sbuf.setLength(0);
		sbuf.append(event.getRenderedMessage());
		return sbuf.toString();
	}

	public boolean ignoresThrowable() 
	{
		return false;
	}
}
