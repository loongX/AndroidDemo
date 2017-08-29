package com.rdm.base.log;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

import com.rdm.base.log.appender.Appender;

/**
 * No-operation implementation of Logger used by NOPLoggerRepository.
 * 
 * @since 1.2.15
 */
public final class NOPLogger extends Logger
{
	/**
	 * Create instance of Logger.
	 * 
	 * @param repo
	 *            repository, may not be null.
	 * @param name
	 *            name, may not be null, use "root" for root logger.
	 */
	public NOPLogger (final String name)
	{
		super(name);
		this.level = Level.OFF;
		this.parent = this;
	}

	/** {@inheritDoc} */
	public void addAppender (final Appender newAppender)
	{
	}

	/** {@inheritDoc} */
	public void assertLog (final boolean assertion, final String msg)
	{
	}

	/** {@inheritDoc} */
	public void callAppenders (final LoggingEvent event)
	{
	}

	/** {@inheritDoc} */
	void closeNestedAppenders ()
	{
	}

	/** {@inheritDoc} */
	public void debug (final Object message)
	{
	}

	/** {@inheritDoc} */
	public void debug (final Object message, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void error (final Object message)
	{
	}

	/** {@inheritDoc} */
	public void error (final Object message, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void fatal (final Object message)
	{
	}

	/** {@inheritDoc} */
	public void fatal (final Object message, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public Enumeration<Appender> getAllAppenders ()
	{
		return new Vector<Appender>().elements();
	}

	/** {@inheritDoc} */
	public Appender getAppender (final String name)
	{
		return null;
	}

	/** {@inheritDoc} */
	public Level getEffectiveLevel ()
	{
		return Level.OFF;
	}

	/** {@inheritDoc} */
	public Level getChainedPriority ()
	{
		return getEffectiveLevel();
	}

	/** {@inheritDoc} */
	public ResourceBundle getResourceBundle ()
	{
		return null;
	}

	/** {@inheritDoc} */
	public void info (final Object message)
	{
	}

	/** {@inheritDoc} */
	public void info (final Object message, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public boolean isAttached (Appender appender)
	{
		return false;
	}

	/** {@inheritDoc} */
	public boolean isDebugEnabled ()
	{
		return false;
	}

	/** {@inheritDoc} */
	public boolean isEnabledFor (final Level level)
	{
		return false;
	}

	/** {@inheritDoc} */
	public boolean isInfoEnabled ()
	{
		return false;
	}

	/** {@inheritDoc} */
	public void l7dlog (final Level priority, final String key,
			final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void l7dlog (final Level priority, final String key,
			final Object[] params, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void log (final Level priority, final Object message,
			final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void log (final Level priority, final Object message)
	{
	}

	/** {@inheritDoc} */
	public void log (final String callerFQCN, final Level level,
			final Object message, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void removeAllAppenders ()
	{
	}

	/** {@inheritDoc} */
	public void removeAppender (Appender appender)
	{
	}

	/** {@inheritDoc} */
	public void removeAppender (final String name)
	{
	}

	/** {@inheritDoc} */
	public void setLevel (final Level level)
	{
	}

	/** {@inheritDoc} */
	public void setPriority (final Level priority)
	{
	}

	/** {@inheritDoc} */
	public void setResourceBundle (final ResourceBundle bundle)
	{
	}

	/** {@inheritDoc} */
	public void warn (final Object message)
	{
	}

	/** {@inheritDoc} */
	public void warn (final Object message, final Throwable t)
	{
	}

	/** {@inheritDoc} */
	public void trace (Object message)
	{
	}

	/** {@inheritDoc} */
	public void trace (Object message, Throwable t)
	{
	}

	/** {@inheritDoc} */
	public boolean isTraceEnabled ()
	{
		return false;
	}

}
