package com.rdm.base.log.layout;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rdm.base.log.LoggingEvent;
import com.rdm.base.log.layout.Layout;

/**
 * TTTLC包括SystemTime,Time,Thread,LoggerName Content
 */
public class TTTNCLayout extends Layout
{
	private boolean					threadPrinting		= true;
	private boolean					loggerNamePrinting	= true;

	protected final StringBuffer	buf					= new StringBuffer(256);
	private final long starttimestamp;

	//近似程序开始时间
	public TTTNCLayout ()
	{
		starttimestamp = System.currentTimeMillis();
	}

	/**
	 * 设置是否输出线程
	 */
	public void setThreadPrinting (boolean threadPrinting)
	{
		this.threadPrinting = threadPrinting;
	}

	/**
	 * 是否输出线程
	 */
	public boolean getThreadPrinting ()
	{
		return threadPrinting;
	}

	public void setCategoryPrefixing (boolean categoryPrefixing)
	{
		this.loggerNamePrinting = categoryPrefixing;
	}

	public boolean getCategoryPrefixing ()
	{
		return loggerNamePrinting;
	}

	public String format (LoggingEvent event)
	{
		buf.setLength(0);

		//系统时间
		Date nowTime=new Date(); 
		SimpleDateFormat time=new SimpleDateFormat("MM-dd HH:mm:ss"); 
		buf.append(time.format(nowTime));
		buf.append(' ');
		
		//App启动时间
		buf.append(""+(event.timeStamp-starttimestamp));
		buf.append(' ');

		if (this.threadPrinting)
		{
			buf.append('[');
			buf.append(event.getThreadName());
			buf.append("] ");
		}
		
		buf.append(event.getLevel().toString());
		buf.append(' ');

		buf.append(event.getTag());
		buf.append(' ');

		buf.append("- ");
		buf.append(event.getRenderedMessage());
		buf.append(LINE_SEP);
		return buf.toString();
	}

	public boolean ignoresThrowable ()
	{
		return true;
	}
}
