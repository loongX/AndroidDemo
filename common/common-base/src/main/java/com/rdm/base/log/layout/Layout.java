package com.rdm.base.log.layout;


import com.rdm.base.log.LoggingEvent;

/**
 * 输出格式
 */
public abstract class Layout
{
	public final static String LINE_SEP = System.getProperty("line.separator");
	public final static int LINE_SEP_LEN  = LINE_SEP.length();

	/**
	 * 格式化输出
	 */
	abstract public String format(LoggingEvent event);
}
