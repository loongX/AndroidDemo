package com.rdm.base.log.appender;

import java.io.IOException;

import com.rdm.base.log.LoggingEvent;

public class WriterAppender extends AppenderSkeleton
{
	protected boolean		immediateFlush	= true;

	/**
	 * This is the {@link QuietWriter quietWriter} where we will write to.
	 */
	protected CountingQuietWriter	qw;

	public WriterAppender ()
	{
	}



	/**
	 * If the <b>ImmediateFlush</b> option is set to <code>true</code>, the
	 * appender will flush at the end of each write. This is the default
	 * behavior. If the option is set to <code>false</code>, then the underlying
	 * stream can defer writing to physical medium to a later time.
	 * 
	 * <p>
	 * Avoiding the flush operation at the end of each append results in a
	 * performance gain of 10 to 20 percent. However, there is safety tradeoff
	 * involved in skipping flushing. Indeed, when flushing is skipped, then it
	 * is likely that the last few log events will not be recorded on disk when
	 * the application exits. This is a high price to pay even for a 20%
	 * performance gain.
	 */
	public synchronized void setImmediateFlush (boolean value)
	{
		immediateFlush = value;
	}


	/**
	 * This method is called by the {@link AppenderSkeleton#doAppend} method.
	 * 
	 * <p>
	 * If the output stream exists and is writable then write a log statement to
	 * the output stream. Otherwise, write a single warning message to
	 * <code>System.err</code>.
	 * 
	 * <p>
	 * The format of the output will depend on this appender's layout.
	 */
	public void append (LoggingEvent event)
	{
		if (!checkEntryConditions())
		{
			return;
		}
		subAppend(event);
	}

	/**
	 * This method determines if there is a sense in attempting to append.
	 * 
	 * <p>
	 * It checks whether there is a set output target and also if there is a set
	 * layout. If these checks fail, then the boolean value <code>false</code>
	 * is returned.
	 */
	protected synchronized boolean checkEntryConditions ()
	{
		if (this.closed || this.qw == null || this.layout == null)
		{
			return false;
		}
		return true;
	}

	/**
	 * Close this appender instance. The underlying stream or writer is also
	 * closed.
	 * 
	 * <p>
	 * Closed appenders cannot be reused.
	 * 
	 * @see #setWriter
	 * @since 0.8.4
	 */
	public synchronized void close ()
	{
		if (this.closed)
			return;
		this.closed = true;
		reset();
	}

	/**
	 * Close the underlying {@link java.io.Writer}.
	 * */
	protected synchronized void closeWriter ()
	{
		if (qw != null)
		{
			try
			{
				qw.close();
			}
			catch (IOException e)
			{
			}
		}
	}



	/**
	 * Actual writing occurs here.
	 * 
	 * <p>
	 * Most subclasses of <code>WriterAppender</code> will need to override this
	 * method.
	 * 
	 * @since 0.9.0
	 */
	protected synchronized void subAppend (LoggingEvent event)
	{
		this.qw.write(this.layout.format(event));
		if (this.immediateFlush)
		{
			this.qw.flush();
		}
	}

	/**
	 * Clear internal references to the writer and other variables.
	 * 
	 * Subclasses can override this method for an alternate closing behavior.
	 */
	protected synchronized void reset ()
	{
		closeWriter();
		this.qw = null;
	}
}
