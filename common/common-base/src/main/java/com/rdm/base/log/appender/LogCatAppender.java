package com.rdm.base.log.appender;


import com.rdm.base.log.Level;
import com.rdm.base.log.LoggingEvent;
import com.rdm.base.log.layout.Layout;
import com.rdm.base.log.appender.AppenderSkeleton;

import android.util.Log;

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
