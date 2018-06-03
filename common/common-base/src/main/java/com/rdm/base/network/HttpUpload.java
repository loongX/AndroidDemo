package com.rdm.base.network;

import android.content.Context;
import android.os.Looper;
import java.io.File;
import java.util.Map;

/**
 *  上传组件对象（使用HTTP上传）
 */
public interface HttpUpload {

    public static class Builder {

        /**
         * 创建一个Uploader对象。默认的，上传事件将回调到主线程上处理。
         * @param context
         * @return
         */
        public static HttpUpload build(Context context) {
            return new DefaultHttpUpload(context, null);
        }

        /**
         * 创建一个Uploader对象。并且将上传事件回调到制指定Looper线程上处理。
         * @param context
         * @param looper
         * @return
         */
        public static HttpUpload build(Context context, Looper looper) {
            return new DefaultHttpUpload(context, looper);
        }
    }

    /**
     * 错误码
     * @author lokierao
     *
     */
    public static enum ErroCode {
        /**
         * 被用户取消
         */
        CANCEL,

        /**
         * 没有网络
         */
        NETWORK_NOT_VAILABLE,

        /**
         * 文件未找到。
         */
        FILE_NOT_FIND,
        /**
         * 网络错误
         */
        NETWORK_ERROR,

        /**
         * HTTP错误
         */
        HTTP_ERROR,
    }

    /**
     * 上传监听器。
     * @author lokierao
     *
     */
    public static interface Listener {

        /**
         * 准备开始上传。
         */
        void onUploadPrepared();


        /**
         * 上传成功。
         * @param httpHeaders 返回http头信息
         * @param httpBody http响应内容
         */
        void onUploadSuccess(Map<String, String> httpHeaders, String httpBody);

        /**
         * 上传失败。
         * @param httpStatusCode
         */
        void onUploadFail(ErroCode code, int httpStatusCode);

        /**
         * 上传进度变化。
         * @param progres 0.0f - 100.0f之间的进度值。
         */
        void onUploadProgressChanged(float progres);
    }

    /**
     * 清理所有的上传内容。
     */
    void clearUploadInfo();

    /**
     * 添加上传参数。
     * @param name 服务器指定的参数名称。
     * @param value  参数值
     * @return
     */
    HttpUpload addParameter(String name, String value);


    /***
     * 添加上传文件。
     * @param name 服务器指定的参数名称。
     * @param file  上传的文件
     * @return
     */
    HttpUpload addFile(String name, File file);

    /**
     * 添加上传数据块。（如果数据块比较大，推荐需要转换为文件的方式上传）
     * @param name 服务器指定的参数名称。
     * @param data 数据块
     * @param fileName （可选）指定文件名称上传。
     * @return
     */
    HttpUpload addData(String name, byte[] data, int start, int end, String fileName);

    /**
     * 设置所有cookies信息
     * @param cookies
     * @return
     */
    HttpUpload setCookies(String cookies);

    /**
    * 开始上传。
    * @param url URL连接
    * @param listener  上传的事件监听器。
    */
    void upload(String url, Listener listener);

    
    /**
     * 取消上传。
     * @return
     */
    boolean cancel();

    /**
     * 是否正在上传中。
     * @return
     */
    boolean isUploading();

}
