package com.example.ilog.log.layout;

import com.example.ilog.log.LoggingEvent;


public class SimpleLayout extends Layout 
{
	StringBuffer sbuf = new StringBuffer(128);

	public SimpleLayout() 
	{
		
	}

	public void activateOptions()
	{
		
	}
  
	public String format(LoggingEvent event)
	{
		sbuf.setLength(0);
		sbuf.append(event.getLevel().toString());
		sbuf.append(" - ");
		sbuf.append(event.getRenderedMessage());
		sbuf.append(LINE_SEP);
		return sbuf.toString();
	}

	public boolean ignoresThrowable() 
	{
		return true;
	}
}