package com.rdm.base.log.appender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import android.util.Log;

import com.rdm.base.log.layout.Layout;

public class FileAppender extends WriterAppender
{
	/**
	 * The name of the log file.
	 */
	protected String	fileName	= null;

	/**
	 * Do we do bufferedIO?
	 */
	protected boolean	bufferedIO	= false;

	/**
	 * Determines the size of IO buffer be. Default is 8K.
	 */
	protected int		bufferSize	= 8 * 1024;

	/**
	 * The default constructor does not do anything.
	 */
	public FileAppender ()
	{
	}

	/**
	 * Instantiate a FileAppender and open the file designated by
	 * <code>filename</code>. The opened filename will become the output
	 * destination for this appender.
	 * 
	 * <p>
	 * If the <code>append</code> parameter is true, the file will be appended
	 * to. Otherwise, the file designated by <code>filename</code> will be
	 * truncated before being opened.
	 */
	public FileAppender (Layout layout, String filename, boolean append) throws IOException
	{
		this.layout = layout;
		this.setFile(filename, append, false, bufferSize);
	}

	/**
	 * Closes the previously opened file.
	 */
	protected synchronized void closeFile ()
	{
		if (this.qw != null)
		{
			try
			{
				this.qw.close();
			}
			catch (java.io.IOException e)
			{
				// Exceptionally, it does not make sense to delegate to an
				// ErrorHandler. Since a closed appender is basically dead.
				Log.e("error","Could not close " + qw, e);
			}
		}
	}

	/**
	 * <p>
	 * Sets and <i>opens</i> the file where the log output will go. The
	 * specified file must be writable.
	 * 
	 * <p>
	 * If there was already an opened file, then the previous file is closed
	 * first.
	 * 
	 * <p>
	 * <b>Do not use this method directly. To configure a FileAppender or one of
	 * its subclasses, set its properties one by one and then call
	 * activateOptions.</b>
	 * 
	 * @param fileName
	 *            The path to the log file.
	 * @param append
	 *            If true will append to fileName. Otherwise will truncate
	 *            fileName.
	 */
	public synchronized void setFile (String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException
	{
		// It does not make sense to have immediate flush and bufferedIO.
		if (bufferedIO)
		{
			setImmediateFlush(false);
		}

		reset();
		FileOutputStream ostream = null;
		try
		{
			ostream = new FileOutputStream(fileName, append);
		}
		catch (FileNotFoundException ex)
		{
			String parentName = new File(fileName).getParent();
			if (parentName != null)
			{
				File parentDir = new File(parentName);
				if (!parentDir.exists() && parentDir.mkdirs())
				{
					ostream = new FileOutputStream(fileName, append);
				}
				else
				{
					throw ex;
				}
			}
			else
			{
				throw ex;
			}
		}
		Writer fw = new OutputStreamWriter(ostream,Charset.defaultCharset());
		if (bufferedIO)
		{
			fw = new BufferedWriter(fw, bufferSize);
		}
		this.qw = new CountingQuietWriter(fw);
		this.fileName = fileName;
		this.bufferedIO = bufferedIO;
		this.bufferSize = bufferSize;
	}


	/**
	 * Close any previously opened file and call the parent's <code>reset</code>
	 * .
	 */
	protected synchronized void reset ()
	{
		closeFile();
		this.fileName = null;
		super.reset();
	}
}
