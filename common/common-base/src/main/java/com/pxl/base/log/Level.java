
package com.pxl.base.log;

public class Level
{
	public final static int OFF_INT = Integer.MAX_VALUE;
	public final static int FATAL_INT = 50000;
	public final static int ERROR_INT = 40000;
	public final static int WARN_INT  = 30000;
	public final static int INFO_INT  = 20000;
	public final static int DEBUG_INT = 10000;
	public final static int VERBOSE_INT = 0;
	public final static int ALL_INT = Integer.MIN_VALUE;
	
	transient int level;
	transient String levelStr;

	/**
	 *
	 */
	final static public Level OFF = new Level(OFF_INT, "OFF");

	/**
	 *
	 */
	final static public Level FATAL = new Level(FATAL_INT, "FATAL");

	/**
	 *
	 */
	final static public Level ERROR = new Level(ERROR_INT, "ERROR");

	/**
	 *
     */
	final static public Level WARN  = new Level(WARN_INT, "WARN");

	/**
	 *
	 */
	final static public Level INFO  = new Level(INFO_INT, "INFO");

	/**
	 *
	 */
	final static public Level DEBUG = new Level(DEBUG_INT, "DEBUG");

	/**
	 *
	 */
	final static public Level VERBOSE = new Level(VERBOSE_INT, "VERBOSE");


	final static public Level ALL = new Level(ALL_INT, "ALL");

	protected Level(int level, String levelStr)
	{
		this.level = level;
		this.levelStr = levelStr;
	}



	public boolean isGreaterOrEqual(Level r) 
	{
		return level >= r.level;
	}
  
	public final int toInt() 
	{
		return level;
	}
	
	public final String toString()
	{
		return levelStr;
	}
}
