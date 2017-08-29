package com.rdm.base.log.appender;

import com.rdm.base.log.LoggingEvent;
import com.rdm.base.log.layout.Layout;

/**
 * Appender的基础抽象类
 */
public abstract class AppenderSkeleton implements Appender
{
	/**
	 * 这个对象可以不用，假如Appender具备自己的格式化实现
	 */
	protected Layout	layout;

	/**
	 * 标志该Appender是否关闭
	 */
	protected boolean	closed	= false;

	public synchronized void setLayout (Layout layout)
	{
		this.layout = layout;
	}
	
	public synchronized Layout getLayout ()
	{
		return layout;
	}

	protected void finalize ()
	{
		if (this.closed)
			return;
		close();
	}


	public synchronized void doAppend (LoggingEvent event)
	{
		if (closed)
		{
			return;
		}
		this.append(event);
	}
	
	abstract protected void append (LoggingEvent event);
}
