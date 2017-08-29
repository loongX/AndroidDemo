package com.rdm.base.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.rdm.base.log.appender.Appender;
import com.rdm.base.log.appender.LogCatAppender;
import com.rdm.base.log.appender.RollingFileAppender;
import com.rdm.base.log.layout.LogCatLayout;
import com.rdm.base.log.layout.TTTNCLayout;


/**
 * 1.Debug LogCat实时输出，文件按启动时间打印
 * 2.Release LogCat不再输出，文件按3*1M输出
 */
public class TLog
{
	private static Logger _logger;
	
	private static boolean _enableFileAppender;
	private static String _directory;
	
	static
	{
		_logger = LogManager.getRootLogger();
	}
	
	public static void enableDebug(boolean debug)
	{

		if(debug)
		{
			setLevel(Level.VERBOSE);
			_logger.removeAllAppenders();
			_logger.addAppender(new LogCatAppender(new LogCatLayout()));
			if(_enableFileAppender)
			{
				try
				{
					RollingFileAppender fileAppender = new RollingFileAppender(new TTTNCLayout(), _directory+File.separator+_logger.getName(), true);
					fileAppender.setMaxBackupIndex(3);
					fileAppender.setMaximumFileSize(10*1024*1024);
					_logger.addAppender(fileAppender);
				}
				catch (IOException e)
				{
					Log.e("error","create file appender fail", e);
				}
			}
		}
		else
		{
			setLevel(Level.INFO);
			_logger.removeAllAppenders();
			if(_enableFileAppender)
			{
				try
				{
					RollingFileAppender fileAppender = new RollingFileAppender(new TTTNCLayout(), _directory+File.separator+_logger.getName(), true);
					fileAppender.setMaxBackupIndex(3);
					fileAppender.setMaximumFileSize(1*1024*1024);
					_logger.addAppender(fileAppender);
				}
				catch (IOException e)
				{
					Log.e("error","create file appender fail", e);
				}
			}
		}
	}
	
	public static void enableFileAppender(boolean enable,String directory)
	{
		_enableFileAppender = true;
		_directory = directory;
	}
	
	public static void setLevel(Level level)
	{
		_logger.setLevel(level);
	}
	
	public static void init()
	{
		_logger = LogManager.getRootLogger();
		if(_logger != null)
		{
			Appender appender = new LogCatAppender(new LogCatLayout());
			_logger.addAppender(appender);
		}
	}
	
	/**
	 * 这里将会打出最详细的信息，多用于开发过程，开发完成之后，最好删除
	 */
	public static void v(String tag,String message)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.verbose(tag,message);
	}
	
	public static void v (String tag, String format , Object... args)
	{
		v(tag,String.format(format, args));
	}

	/**
	 * 这里将会打出调试信息，需求开发完成之后保留的信息
	 */
	public static void d(String tag,String message)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.debug(tag,message);
	}
	
	public static void d(String tag,String format,Object... args)
	{
		d(tag,String.format(format, args));
	}
	
	/**
	 * 这里将会打出关键的数据信息
	 */
	public static void i(String tag,String message)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.info(tag,message);
	}
	
	public static void i(String tag,String format,Object... args)
	{
		i(tag,String.format(format, args));
	}
	
	/**
	 * 这里将会打出一些警告，一般用于程序走了我们不想让他走的路径
	 */
	public static void w(String tag,String message)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.warn(tag,message);
	}
	
	public static void w(String tag,String format,Object... args)
	{
		w(tag,String.format(format, args));
	}
	
	public static void e (String tag, String error)
	{
		e(tag, error, null);
	}
	
	/**
	 * 这里将会打出一些错误，但不引起崩溃的error
	 */
	public static void e(String tag,String message,Throwable t)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.error(tag,message + '\n' + getStackTraceString(t));

	}
	
	public static void f(String tag , String message)
	{
		f(tag,message,null);
	}
	
	/**
	 * 这里将会打出一些致命的错误
	 */
	public static void f(String tag,String message,Throwable t)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.fatal(tag,message + '\n' + getStackTraceString(t));
	}

    public static String getStackTraceString(Throwable tr) 
    {
        if (tr == null) 
        {
            return "";
        }

        Throwable t = tr;
        while (t != null) 
        {
            if (t instanceof UnknownHostException) 
            {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    public static void println(Level level, String tag, String msg) 
    {
    	if(_logger == null)
		{
			init();
		}
		_logger.println(level,tag,msg);
    }
    
    public static void println(Level level,String tag,byte[] data)
    {
    	if(_logger == null)
		{
			init();
		}
    	StringBuilder sb = new StringBuilder();
    	if(data != null && data.length >0)
    	{
    		for(int i = 0;i<data.length;i++)
    		{
    			sb.append(String.format(" %02x", data[i]));
    		}
    	}
    	_logger.println(level,tag,sb.toString());
    }
	
	public static void printStackTrace(Throwable thr)
	{
		if(_logger == null)
		{
			init();
		}
		_logger.println(Level.ERROR,"exception",getStackTraceString(thr));
	}

	public static void list(String tag,String title,List list){
		StringBuffer sb = new StringBuffer(title);
		sb.append(" : [");
		int count = list.size();
		for (int i = 0 ; i < count ; i++){
			if (i != 0){
				sb.append(",");
			}
			sb.append(list.get(i).toString());
		}
		sb.append(" ]");
		TLog.v(tag,sb.toString());
	}

	public static class TLogger {

		private boolean mIsDisabled = false;

		private String mTag;
		private String mPrefix;
		private Class<?> mClasz;

		public TLogger(String tag) {
			this(tag, null, null);
		}


		public TLogger(String tag, String prefix) {
			this(tag, null, prefix);
		}

		public TLogger setEnabeState(boolean isEnabled) {
			mIsDisabled = !isEnabled;
			return this;
		}

		public TLogger(String tag, Class<?> clasz, String prefix) {
			mTag = tag;
			mPrefix = prefix;
			mClasz = clasz;

			if (tag == null && clasz != null) {
				mTag = clasz.getSimpleName();
				mClasz = null;
			}
		}


		public void v(String text) {
			if (mIsDisabled) return;
			TLog.v(mTag, formatText(text));
		}



		public void d(String text) {
			if (mIsDisabled) return;
			TLog.d(mTag + ":" + mPrefix, formatText(text));
		}

		public void i(String text) {
			if (mIsDisabled) return;
			TLog.i(mTag + ":" + mPrefix, formatText(text));
		}

		public void w(String text) {
			if (mIsDisabled) return;
			TLog.w(mTag + ":" + mPrefix, formatText(text));
		}

		public void e(String text) {
			if (mIsDisabled) return;
			TLog.e(mTag + ":" + mPrefix, formatText(text));
		}

		private String formatText(String text) {
			StringBuilder builder = new StringBuilder();

			if (mClasz != null){
				builder.append("[");
				builder.append(mClasz.getSimpleName());
				builder.append("]");
			}

			if (mPrefix != null) {
				builder.append("<");
				builder.append(mPrefix);
				builder.append(">");
			}

			builder.append("(");
			builder.append(Thread.currentThread().getId());
			builder.append(") ");

			return builder.append(text).toString();
		}


		//专门为了记录IM sdk的日志开发的逻辑，其他日志请不要调用
		private static String MYLOGFILEName = "_im_log.txt";// 本类输出的日志文件名称
		private static String myLogSdf = new String("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
		private static String MYLOG_PATH_SDCARD_DIR=null;// 日志文件在sdcard中的路径
		private static int SDCARD_LOG_FILE_SAVE_DAYS = 5;// sd卡中日志文件的最多保存天数
		private static String logfile = "yyyy-MM-dd";// 日志文件格式

		public void f(String text){
			try{
				writeLogtoFile(DateFormat.format(myLogSdf,System.currentTimeMillis())+":"+text);
			}catch(Exception e){

			}

		}


		/**
		 * 打开日志文件并写入日志
		 *
		 * @return
		 * **/
		String nowLogFileDate = "";
		private void writeLogtoFile( String text) {// 新建或打开日志文件
			//d(text);
			Date nowtime = new Date();
			String needWriteFiel = DateFormat.format(logfile,System.currentTimeMillis()).toString();
			if(MYLOG_PATH_SDCARD_DIR==null || "".equals(MYLOG_PATH_SDCARD_DIR)){
				MYLOG_PATH_SDCARD_DIR = getSDPath()+"/tgp";
			}
			File dir = new File(MYLOG_PATH_SDCARD_DIR);
			if(!dir.exists()){
				dir.mkdirs();
			}
			File file = new File(MYLOG_PATH_SDCARD_DIR, needWriteFiel + MYLOGFILEName);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
				BufferedWriter bufWriter = new BufferedWriter(filerWriter);
				bufWriter.write(text);
				bufWriter.newLine();
				bufWriter.close();
				filerWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String getSDPath(){
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
			if   (sdCardExist)
			{
				sdDir = Environment.getExternalStorageDirectory();//获取跟目录
			}
			return sdDir.getPath();

		}



		/**
		 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
		 * */
		private static Date getDateBefore() {
			Date nowtime = new Date();
			Calendar now = Calendar.getInstance();
			now.setTime(nowtime);
			now.set(Calendar.DATE, now.get(Calendar.DATE)
					- SDCARD_LOG_FILE_SAVE_DAYS);
			return now.getTime();
		}
	}
}
