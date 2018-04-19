package com.pxl.base.log.appender;


import android.util.Log;

import com.pxl.base.log.Level;
import com.pxl.base.log.LoggingEvent;
import com.pxl.base.log.layout.Layout;

public class LogCatAppender extends AppenderSkeleton 
{
	public LogCatAppender() 
	{
		
	}

	public LogCatAppender(Layout layout) 
	{
		setLayout(layout);
	}
	
	public synchronized void append(LoggingEvent event)
	{
		String message = layout.format(event);
		switch (event.getLevel().toInt()) 
		{
		case Level.ALL_INT:
		case Level.VERBOSE_INT:
				Log.v(event.getTag(), message);
			break;
		
		case Level.DEBUG_INT:
				Log.d(event.getTag(), message);
			break;
		case Level.INFO_INT:
				Log.i(event.getTag(), message);
			break;
		case Level.WARN_INT:
				Log.w(event.getTag(), message);
			break;
		case Level.ERROR_INT:
		case Level.FATAL_INT:
				Log.e(event.getTag(), message);
			break;
		case Level.OFF_INT:
			break;
		default:
			break;
		}
	}

	@Override
	public void close ()
	{
		
	}
}
