package com.example.basedemo.ilog.log;


public class LogManager
{
	static private Logger rootLogger;

	static
	{
		rootLogger = new Logger("root");
		rootLogger.setLevel(Level.VERBOSE);
	}

	/**
	 * Retrieve the appropriate root logger.
	 */
	public static Logger getRootLogger ()
	{
		return rootLogger;
	}
}
