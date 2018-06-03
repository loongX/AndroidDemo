package com.rdm.base.log;

public class LoggingEvent 
{

	private String tag;

	transient public final String fqnOfCategoryClass;

	final public String categoryName;

	transient public Level level;

	transient private Object message;

	private String renderedMessage;

	private String threadName;

	public final long timeStamp;


	public LoggingEvent(String fqnOfCategoryClass, String tag, Logger logger,Level level, Object message) 
	{
		this.fqnOfCategoryClass = fqnOfCategoryClass;
		this.tag = tag;
		this.categoryName = logger.getName();
		this.level = level;
		this.message = message;
		this.timeStamp = System.currentTimeMillis();
	}

	/**
	 * Return the level of this event. Use this form instead of directly
	 * accessing the <code>level</code> field.
	 */
	public Level getLevel()
	{
		return (Level) level;
	}

	/**
	 * Return the name of the logger. Use this form instead of directly
	 * accessing the <code>categoryName</code> field.
	 */
	public String getLoggerName() 
	{
		return categoryName;
	}

	public String getRenderedMessage() 
	{
		if (renderedMessage == null && message != null)
		{
			if (message instanceof String)
				renderedMessage = (String) message;
			else
				renderedMessage = message.toString();
		}
		return renderedMessage;
	}

	public String getThreadName()
	{
		if (threadName == null)
			threadName = (Thread.currentThread()).getName();
		return threadName;
	}

	public final long getTimeStamp() 
	{
		return timeStamp;
	}

	public String getFQNOfLoggerClass()
	{
		return fqnOfCategoryClass;
	}

	public String getTag()
	{
		if (tag != null) 
		{
			return tag;
		}
		return categoryName;
	}
}
