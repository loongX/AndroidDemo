package com.rdm.base.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.rdm.base.ThreadManager;
import com.rdm.common.ILog;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DefaultHttpUpload implements HttpUpload
{

	private static final int UPLOAD_BUFFER = 20 * 1024;

	private static final String TAG = "DefaultUploader";

	private boolean isUploading;

	private volatile boolean mCancel;

	private Context mContext;

	private Vector<Element> mElements = new Vector<Element>();

	private Charset mCharset;
	private Handler mCallbackHandler;

	private String mCookies = null;

	private static class Element
	{

		String name;
		Object data;
		String fileName;
	}

	public DefaultHttpUpload(Context context, Looper looper)
	{
		if (context == null)
		{
			throw new NullPointerException("context could't be null.");
		}
		Looper mLooper;
		if (looper == null)
		{
			mLooper = Looper.getMainLooper();
		}
		else
		{
			mLooper = looper;
		}
		mContext = context;
		mCharset = Charset.forName("utf8");
		mCallbackHandler = new Handler(mLooper);
	}

	@Override
	public boolean cancel ()
	{
		mCancel = true;
		absortDownload();
		return true;
	}

	@Override
	public boolean isUploading ()
	{
		return isUploading;
	}

	@Override
	public HttpUpload addFile (String name, File file)
	{
		Element e = new Element();
		e.name = name;
		e.data = file;
		e.fileName = file.getName();

		mElements.add(e);
		return this;
	}

	@Override
	public HttpUpload addData (String name, byte[] data, int start, int end, String fileName)
	{

		byte[] uploadData;
		if (start == 0 && end == data.length)
		{
			uploadData = data;
		}
		else
		{
			uploadData = new byte[end - start];
			System.arraycopy(data, start, uploadData, 0, uploadData.length);
		}
		Element e = new Element();
		e.name = name;
		e.data = uploadData;
		e.fileName = fileName;

		if (e.fileName == null || e.fileName.length() < 1)
		{
			e.fileName = "undefine";
		}

		mElements.add(e);
		return this;
	}

	@Override
	public HttpUpload setCookies(String cookie) {
		mCookies =cookie;
		return this;
	}

	@Override
	public HttpUpload addParameter (String name, String value)
	{
		Element e = new Element();
		e.name = name;
		e.data = value;

		mElements.add(e);
		return this;
	}

	@Override
	public void clearUploadInfo ()
	{
		mElements.clear();

	}

	@Override
	public synchronized void upload (final String url, Listener listener)
	{
		checkBeforeUpload();

		if (!isNetworkAvailable(mContext))
		{
			callOnUploadFailed(listener, ErroCode.NETWORK_NOT_VAILABLE, -1);
			return;
		}

		final Listener theListner = listener;
		final HttpClient httpClient = getHttpClient();
		final HttpPost httppost = new HttpPost(url);
		final MultipartEntity entity = buildMultipartEntity(listener);

		if(!TextUtils.isEmpty(mCookies)){
			httppost.setHeader("Cookie",mCookies);
		}

		mCurrentHttpGet = httppost;

		if (entity == null)
		{
			throw new RuntimeException("Empty upload data.");
		}

		isUploading = true;
		mCancel = false;

		ThreadManager.execute(new Runnable() {
			@Override
			public void run() {
				doUpload(theListner, httppost, entity, httpClient);
			}
		});
	}

	private void doUpload (Listener listener, HttpPost httppost, MultipartEntity entity, HttpClient httpClient)
	{
		HttpResponse httpResponse;
		callOnOnStart(listener);
		try
		{
			httppost.setEntity(entity);
			httpResponse = httpClient.execute(httppost);

			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = loadAsText(httpResponse);

			if (statusCode == HttpStatus.SC_OK)
			{
				List<NameValuePair> headerList = getHeaders(httpResponse);
				Map<String, String> header = new HashMap<String, String>();
				for (NameValuePair pair : headerList)
				{
					header.put(pair.getName(), pair.getValue());
				}
				callOnUploadSuccess(listener, header, response);
			}
			else
			{
				ILog.d(TAG, " on upload fail : status code = " + statusCode + "\n" + response);
				callOnUploadFailed(listener, ErroCode.HTTP_ERROR, statusCode);
			}
		}
		catch (FileNotFoundException e)
		{
			callOnUploadFailed(listener, ErroCode.FILE_NOT_FIND, -1);
			ILog.e(TAG, "file not find", e);

		}
		catch (Exception e)
		{
			if (mCancel)
			{
				callOnUploadFailed(listener, ErroCode.CANCEL, -1);
				ILog.d(TAG, "cancel download");
			}
			else
			{
				ILog.e(TAG, "download error: ", e);
				callOnUploadFailed(listener, ErroCode.NETWORK_ERROR, -1);
			}
		}
	}

	private HttpPost mCurrentHttpGet = null;

	private synchronized void absortDownload ()
	{
		try
		{
			if (mCurrentHttpGet != null)
			{
				mCurrentHttpGet.abort();
				mCurrentHttpGet = null;
			}

		}
		catch (Throwable ex)
		{
			//do nothing
		}

        /*try {
			if (mCurrentStream != null) {
                mCurrentStream.close();
                mCurrentStream = null;
            }
        } catch (Throwable ex) {

        }*/
	}

	private synchronized void onUploadEnd ()
	{
		mCurrentHttpGet = null;
		isUploading = false;

	}

	private synchronized boolean isCancel ()
	{
		return mCancel;
	}

	private void callOnUploadProgressChanged (final Listener listener, final long current, final long totalLength)
	{
		if (listener != null)
		{
			mCallbackHandler.post(new Runnable()
			{
				@Override
				public void run ()
				{
					double value = ((double) current) / totalLength;
					value = value * 100;
					if (value > 100)
					{
						value = 100;
					}
					listener.onUploadProgressChanged((float) value);
				}
			});
		}

	}

	private void callOnUploadFailed (final Listener listener, final ErroCode code, final int httpStatusCode)
	{
		if (listener != null)
		{
			mCallbackHandler.post(new Runnable()
			{

				@Override
				public void run ()
				{
					listener.onUploadFail(code, httpStatusCode);
				}
			});
		}
		onUploadEnd();
	}

	private void callOnOnStart (final Listener listener)
	{
		if (listener != null)
		{
			mCallbackHandler.post(new Runnable()
			{

				@Override
				public void run ()
				{
					listener.onUploadPrepared();
				}
			});
		}

	}

	private void callOnUploadSuccess (final Listener listener, final Map<String, String> httpHeaders,
			final String HttpBody)
	{
		if (listener != null)
		{
			mCallbackHandler.post(new Runnable()
			{
				@Override
				public void run ()
				{
					listener.onUploadSuccess(httpHeaders, HttpBody);
				}
			});
		}
		onUploadEnd();

	}

	private MultipartEntity buildMultipartEntity (Listener listener)
	{
		if (mElements.isEmpty())
		{
			return null;
		}
		final MultipartEntity entity = new MultipartEntity();
		try
		{
			for (Element ele : mElements)
			{

				if (ele.data instanceof String)
				{
					String text = (String) ele.data;
					entity.addPart(ele.name, new StringBody(text, mCharset));
				}
				else if (ele.data instanceof byte[])
				{
					byte[] bytes = (byte[]) ele.data;
					ByteArrayBody byteArrayBody = new ByteArrayBody(bytes, ele.fileName);
					entity.addPart(ele.name, byteArrayBody);
				}
				else if (ele.data instanceof File)
				{
					FileBody fileBody = new FileBody((File) ele.data, listener);
					entity.addPart(ele.name, fileBody);
				}

			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}

		return entity;
	}

    /*    
		private InputStream getInputStream() {
        return null;
    }*/

	private void checkBeforeUpload ()
	{
		if (isUploading())
		{
			throw new RuntimeException("uploader is uploading. you must cancel() first before upload a new task.");
		}
	}

	protected String loadAsText (HttpResponse response) throws Exception
	{
		HttpEntity entity = response.getEntity();
		if (entity == null)
		{
			return "";
		}
		InputStream is = entity.getContent();
		if (is == null)
		{
			return "";
		}
		ByteArrayOutputStream fos = new ByteArrayOutputStream(1024 * 10);
		final byte[] buffer = new byte[5 * 1024];
		try
		{
			//file.deleteOnExit();
			int nbRead;
			while (-1 != (nbRead = is.read(buffer)))
			{
				fos.write(buffer, 0, nbRead);
			}

		}
		finally
		{
			closeQuietly(fos);
			closeQuietly(is);

		}

		String encode = getContentCharset(response, new ByteArrayInputStream(fos.toByteArray()));

		return new String(fos.toByteArray(), encode);
	}

	public String getContentCharset (HttpResponse response, InputStream in)
	{
		List<NameValuePair> headers = getHeaders(response);
		String charset = getContentCharsetOrNull(headers, in);
		if (charset != null)
		{
			return charset;
		}

		return "utf8";
	}

	public String getContentCharsetOrNull (List<NameValuePair> headers, InputStream is)
	{
		try
		{
			return EncodingSniffer.sniffEncoding(headers, is);
		}
		catch (final IOException e)
		{
			return null;
		}
		finally
		{
			closeQuietly(is);
		}
	}

	public List<NameValuePair> getHeaders (HttpResponse response)
	{
		Header[] headers = response.getAllHeaders();

		int size = headers != null ? headers.length : 0;
		List<NameValuePair> list = new ArrayList<NameValuePair>(size);
		if (size > 0)
		{
			for (Header entry : headers)
			{
				list.add(new BasicNameValuePair(entry.getName(), entry.getValue()));
			}
		}
		return list;
	}

	class FileBody extends org.apache.http.entity.mime.content.FileBody
	{
		HttpUpload.Listener mProgressListner = null;

		public FileBody (File file, HttpUpload.Listener listener)
		{
			super(file);
			mProgressListner = listener;
		}

		@Override
		public void writeTo (final OutputStream out) throws IOException
		{

			long length = getContentLength();

			final InputStream in = new FileInputStream(getFile());
			try
			{
				final byte[] tmp = new byte[UPLOAD_BUFFER];
				int l;
				long var = 0;
				while ((l = in.read(tmp)) != -1 && !isCancel())
				{
					out.write(tmp, 0, l);
					var += l;
					callOnUploadProgressChanged(mProgressListner, var, length);
				}

				if (isCancel())
				{
					out.close();
				}
				else
				{
					out.flush();
				}

			}
			finally
			{
				in.close();
			}
		}

	}

	//private static HttpClient customerHttpClient;

	private static synchronized HttpClient getHttpClient ()
	{
		// if (null == customerHttpClient) {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setTimeout(params, 1000);
		int ConnectionTimeOut = 3000;

		HttpConnectionParams.setConnectionTimeout(params, ConnectionTimeOut);
		HttpConnectionParams.setSoTimeout(params, 4000);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		return new DefaultHttpClient(conMgr, params);
		// customerHttpClient = new DefaultHttpClient(conMgr, params);
		// }
		//  return customerHttpClient;
	}

	public static void closeQuietly (Closeable closeable)
	{
		try
		{
			if (closeable != null)
			{
				closeable.close();
			}
		}
		catch (IOException ioe)
		{
			// ignore
		}
	}

	public static boolean isNetworkAvailable (Context context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (NetworkInfo anInfo : info)
				{
					if (anInfo.isAvailable())
					{
						return true;

					}
				}
			}
		}
		return false;
	}
}
