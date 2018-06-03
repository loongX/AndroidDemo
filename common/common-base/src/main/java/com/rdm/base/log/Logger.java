package com.rdm.base.log;

import java.util.Enumeration;
import java.util.Vector;

import com.rdm.base.log.AppenderAttachable;
import com.rdm.base.log.Level;
import com.rdm.base.log.LogManager;
import com.rdm.base.log.Logger;
import com.rdm.base.log.LoggingEvent;
import com.rdm.base.log.appender.Appender;

public class Logger implements AppenderAttachable 
{
	private static final String FQCN = Logger.class.getName();
	
	/**
	 * Logger��Name
	 */
	protected String   name;

	volatile protected Level level;
	
	/**
	 * Logger��parent��ÿһ��Logger����һ��parent
	 */
	volatile protected Logger parent;

	protected Vector<Appender> appenderList;

	/**
	 * �Ƿ�ʹ��parent������ѡ��
	 */
	protected boolean additive = true;


	protected Logger(String name) 
	{
		this.name = name;
	}

	/**
	 * ����Appender
	 */
	synchronized public void addAppender(Appender newAppender) 
	{
		if(newAppender == null)
			return;
    
		if(appenderList == null)
		{
			appenderList = new Vector<Appender>(1);
		}
		
		if(!appenderList.contains(newAppender))
			appenderList.addElement(newAppender);
	}


	public synchronized void callAppenders(LoggingEvent event) 
	{
		for(Logger c = this; c != null; c=c.parent) 
		{
			synchronized(c) 
			{
				if(appenderList != null)
				{
					for(Appender appender : appenderList)
					{
						appender.doAppend(event);
					}
				}
				
				if(!c.additive) 
				{
					break;
				}
			}
		}
	}

	/**
	 * �ر����е�Appender
	 */
	synchronized void closeNestedAppenders() 
	{
		Enumeration<Appender> enumeration = this.getAllAppenders();
		if(enumeration != null) 
		{
			while(enumeration.hasMoreElements()) 
			{
				Appender a = (Appender) enumeration.nextElement();
				if(a instanceof AppenderAttachable) 
				{
					a.close();
				}
			}
		}
	}

	public void debug(String tag,String message) 
	{
		if(Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN, tag,Level.DEBUG, message);
	}
	


	public void error(String tag,Object message)
	{
		if(Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN,tag,Level.ERROR, message);
	}

	public void fatal(String tag,String message)
	{
		if(Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN, tag,Level.FATAL, message);
	}
	
	public void info(String tag,String message)
	{
		if(Level.INFO.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN,tag, Level.INFO, message);
	}
	
	public void verbose(String tag,Object message)
	{
		if (Level.VERBOSE.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN, tag,Level.VERBOSE, message);
	}
	
	
	protected void println(Level level, String tag,Object message) 
	{
		if(level.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN,tag,level,message);
	}
	
	/**
	 * ����һ��event�����Ҽ�¼��log
	 */
	protected void forcedLog(String fqcn, Level level, Object message) 
	{
		forcedLog(fqcn,null,level,message);
	}
	
	
	protected void forcedLog(String fqcn, String tag,Level level, Object message) 
	{
		callAppenders(new LoggingEvent(fqcn, tag,this, level, message));
	}


	/**
	 * ��ȡ��ǰ��Additiveֵ
	 */
	public boolean getAdditivity() 
	{
		return additive;
	}

	synchronized public Enumeration<Appender> getAllAppenders() 
	{
		if(appenderList == null)
			return null;
		else 
			return appenderList.elements(); 
	}

	public Level getEffectiveLevel() 
	{
		for(Logger c = this; c != null; c=c.parent) 
		{
			if(c.level != null)
				return c.level;
		}
		return null; // If reached will cause an NullPointerException.
	}

	public final String getName() 
	{
		return name;
	}

	final public Logger getParent() 
	{
		return this.parent;
	}

	final public Level getLevel() 
	{
		return this.level;
	}
	
	public synchronized boolean isAttached(Appender appender)
	{
		if(appenderList == null || appender == null)
			return false;

		for(Appender tmpAppender : appenderList)
		{
			if(tmpAppender == appender)
				  return true;
		}
		return false;  
	}




	public boolean isVerboseEnabled() 
	{
        return Level.VERBOSE.isGreaterOrEqual(this.getEffectiveLevel());
	}
	
	public boolean isInfoEnabled() 
	{
		return Level.INFO.isGreaterOrEqual(this.getEffectiveLevel());
	}
	
	public boolean isDebugEnabled() 
	{
		return Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel());
	}

	public boolean isEnabledFor(Level level)
	{
		return level.isGreaterOrEqual(this.getEffectiveLevel());
	}

	
	public void log(Level level, String message) 
	{
		this.log(FQCN, level, message);
	}

	public void log(String callerFQCN, Level level, Object message) 
	{
		if(level.isGreaterOrEqual(this.getEffectiveLevel())) 
		{
			forcedLog(callerFQCN, level, message);
		}
	}

	synchronized public void removeAllAppenders() 
	{
		if(appenderList != null) 
		{
			for(Appender appender : appenderList)
			{
				appender.close();
			}
			appenderList.removeAllElements();
		      appenderList = null; 
		}
	}

	synchronized public void removeAppender(Appender appender)
	{
		if(appender == null || appenderList == null) 
			return;
		appenderList.removeElement(appender); 
	}

	public void setAdditivity(boolean additive) 
	{
		this.additive = additive;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public void warn(String tag,String message) 
	{
		if(Level.WARN.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN,tag, Level.WARN, message);
	}

	public static Logger getRootLogger() 
	{
		return LogManager.getRootLogger();
	}
}
