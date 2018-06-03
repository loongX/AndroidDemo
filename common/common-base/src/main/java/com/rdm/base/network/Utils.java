package com.rdm.base.network;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import com.rdm.common.ILog;

class Utils {

    /**
     * The number of bytes in a kilobyte.
     */
    public static final int ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final int ONE_MB = ONE_KB * ONE_KB;


    private static final int FILE_COPY_BUFFER_SIZE = ONE_MB * 30;
    private static final int DEFAUL_BUFFER_SIZE = ONE_KB * 30;

    /**
     * Copies a file to a new location.
     * <p>
     * This method copies the contents of the specified source file to the
     * specified destination file. The directory holding the destination file is
     * created if it does not exist. If the destination file exists, then this
     * method will overwrite it.
     * <p>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * {@code true} tries to preserve the file's last modified date/times using
     * {@link java.io.File#setLastModified(long)}, however it is not guaranteed that the
     * operation will succeed. If the modification operation fails, no
     * indication is provided.
     * 
     * @param srcFile
     *            an existing file to copy, must not be {@code null}
     * @param destFile
     *            the new file, must not be {@code null}
     * @param preserveFileDate
     *            true if the file date of the copy should be the same as the
     *            original
     * 
     * @throws NullPointerException
     *             if source or destination is {@code null}
     * @throws java.io.IOException
     *             if source or destination is invalid
     * @throws java.io.IOException
     *             if an IO error occurs during copying
     */
    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcFile.exists() == false) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination '" + parentFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }

    /**
     * Internal copy file method.
     * 
     * @param srcFile
     *            the validated source file, must not be {@code null}
     * @param destFile
     *            the validated destination file, must not be {@code null}
     * @param preserveFileDate
     *            whether to preserve the file date
     * @throws java.io.IOException
     *             if an error occurs
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } 
        finally 
        {
            closeQuietly(output);
            closeQuietly(fos);
            closeQuietly(input);
            closeQuietly(fis);
        }

        if (srcFile.length() != destFile.length()) 
        {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) 
        {
            boolean flag = destFile.setLastModified(srcFile.lastModified());
            if (!flag)
            {
            	ILog.v("Common", "Utils doCopyFile setLastModified fail");
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    
    public static long copyLarge(Reader input, Writer output, char [] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static boolean saveStringAsFile(File target, String text, String encode) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(target);
            byte[] data = text.getBytes(encode);
            byte[] buffer = new byte[3 * 1024];
            copyLarge(new ByteArrayInputStream(data), out, buffer);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            closeQuietly(out);

        }
        return false;
    }

    public static boolean saveBytesAsFile(File target, byte[] bytes) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(target);
            byte[] data = bytes;
            byte[] buffer = new byte[3 * 1024];
            copyLarge(new ByteArrayInputStream(data), out, buffer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(out);

        }
        return false;
    }

    public static String readFileAsString(File file, String encode) {
        FileInputStream ifs = null;
        InputStreamReader in = null;
        try {
            StringBuilderWriter sw = new StringBuilderWriter();

            ifs = new FileInputStream(file);
            in = new InputStreamReader(ifs, encode);
            copyLarge(in, sw, new char[DEFAUL_BUFFER_SIZE]);
            return sw.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            closeQuietly(in);
            closeQuietly(ifs);

        }
        return null;
    }
    
    public static class StringBuilderWriter extends Writer implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -5868613523139909572L;
        private final StringBuilder builder;

        /**
         * Construct a new {@link StringBuilder} instance with default capacity.
         */
        public StringBuilderWriter() {
            this.builder = new StringBuilder();
        }

        /**
         * Construct a new {@link StringBuilder} instance with the specified
         * capacity.
         * 
         * @param capacity
         *            The initial capacity of the underlying
         *            {@link StringBuilder}
         */
        public StringBuilderWriter(int capacity) {
            this.builder = new StringBuilder(capacity);
        }

        /**
         * Construct a new instance with the specified {@link StringBuilder}.
         * 
         * @param builder
         *            The String builder
         */
        public StringBuilderWriter(StringBuilder builder) {
            this.builder = builder != null ? builder : new StringBuilder();
        }

        /**
         * Append a single character to this Writer.
         * 
         * @param value
         *            The character to append
         * @return This writer instance
         */
        @Override
        public Writer append(char value) {
            builder.append(value);
            return this;
        }

        /**
         * Append a character sequence to this Writer.
         * 
         * @param value
         *            The character to append
         * @return This writer instance
         */
        @Override
        public Writer append(CharSequence value) {
            builder.append(value);
            return this;
        }

        /**
         * Append a portion of a character sequence to the {@link StringBuilder}
         * .
         * 
         * @param value
         *            The character to append
         * @param start
         *            The index of the first character
         * @param end
         *            The index of the last character + 1
         * @return This writer instance
         */
        @Override
        public Writer append(CharSequence value, int start, int end) {
            builder.append(value, start, end);
            return this;
        }

        /**
         * Closing this writer has no effect.
         */
        @Override
        public void close() {
        }

        /**
         * Flushing this writer has no effect.
         */
        @Override
        public void flush() {
        }

        /**
         * Write a String to the {@link StringBuilder}.
         * 
         * @param value
         *            The value to write
         */
        @Override
        public void write(String value) {
            if (value != null) {
                builder.append(value);
            }
        }

        /**
         * Write a portion of a character array to the {@link StringBuilder}.
         * 
         * @param value
         *            The value to write
         * @param offset
         *            The index of the first character
         * @param length
         *            The number of characters to write
         */
        @Override
        public void write(char[] value, int offset, int length) {
            if (value != null) {
                builder.append(value, offset, length);
            }
        }

        /**
         * Return the underlying builder.
         * 
         * @return The underlying builder
         */
        public StringBuilder getBuilder() {
            return builder;
        }

        /**
         * Returns {@link StringBuilder#toString()}.
         * 
         * @return The contents of the String builder.
         */
        @Override
        public String toString() {
            return builder.toString();
        }
    }

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String getHeaderValue(HttpResponse r, String name) {
        Header header = r.getFirstHeader(name);
        return header != null ? header.getValue() : null;
    }

    public static String getUTF8(byte[] data) {
        return getUTF8(data, 0, data.length);
    }
    
    public static String getUTF8(byte[] data, int start, int end) {
        try {
            return new String(data, start, end, "utf8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(String str) {
        try {
            return str.getBytes("utf8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
