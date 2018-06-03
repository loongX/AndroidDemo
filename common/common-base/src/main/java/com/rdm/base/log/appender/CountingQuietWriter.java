package com.rdm.base.log.appender;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class CountingQuietWriter extends FilterWriter
{
	protected long	count;
	
	
	
	public CountingQuietWriter (Writer writer)
	{
		super(writer);
	}

	public void write (String string)
	{
		try
		{
			out.write(string);
			count += string.length();
		}
		catch (IOException e)
		{

		}
	}

	public long getCount ()
	{
		return count;
	}

	public void setCount (long count)
	{
		this.count = count;
	}
	
	public void flush()
	{
		try 
		{
			out.flush();
		} catch (Exception e)
		{
			
		}
	}
}
