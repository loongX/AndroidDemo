package com.rdm.base.log;

public class TLogLog
{
	private static boolean	debugEnabled	= false;
	
	public final static String PREFIX = "TLogLog : ";
	public final static String ERR_PREFIX = "TLogLog Error : ";
	public final static String WARN_PREFIX = "TLogLog WARN : ";
	
	public static void setDebug(boolean debug)
	{
		debugEnabled = debug;
	}

	static public void setInternalDebugging (boolean enabled)
	{
		debugEnabled = enabled;
	}

	public static void debug (String msg)
	{
		if (debugEnabled)
		{
			System.out.println(PREFIX + msg);
		}
	}

	public static void debug (String msg, Throwable t)
	{
		if (debugEnabled)
		{
			System.out.println(PREFIX + msg);
			if (t != null)
				t.printStackTrace(System.out);
		}
	}

	public static void error (String msg)
	{
		System.err.println(ERR_PREFIX + msg);
	}

	public static void error (String msg, Throwable t)
	{
		System.err.println(ERR_PREFIX + msg);
		if (t != null)
		{
			t.printStackTrace();
		}
	}

	public static void warn (String msg)
	{
		System.err.println(WARN_PREFIX + msg);
	}

	public static void warn (String msg, Throwable t)
	{
		System.err.println(WARN_PREFIX + msg);
		if (t != null)
		{
			t.printStackTrace();
		}
	}
}
