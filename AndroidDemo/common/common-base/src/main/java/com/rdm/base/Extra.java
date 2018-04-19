package com.rdm.base;

import com.rdm.common.ILog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 一种数据包。
 * Created by Rao on 2015/2/7.
 */
public class Extra implements Serializable{

    private static final String TAG = "Bundle";

    //顺序不能再变，否则的话，不能正确读取之前保存的数据。
    private static final Class[] CLASS_SUPPORTED = new Class[]{Integer.class,byte[].class,String.class,Boolean.class,Float.class,Long.class,Character.class,Short.class,Byte.class,Double.class};

    HashMap<String, Object> mMap = null;

    private byte[] mParcelData = null;

    public Extra(){
        mMap = new HashMap<String, Object>();
    }

   public Extra(byte[] data){
        if(data==null){
            data = new byte[0];
        }
        mParcelData = data;
    }

    public Extra(Extra extra){
        if(extra.mMap!= null){
            mMap = new HashMap<String,Object>(extra.mMap);
        }else{
            mParcelData = extra.mParcelData;
            if(mParcelData==null){
                throw new RuntimeException();
            }
        }
    }

    private Extra(HashMap<String,Object> map){
        mMap = map;
        if(mMap == null){
            throw new RuntimeException();
        }
    }

    public byte[] toByteArray(){
        unparcel();
        try {
            return save(mMap);
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public  static Extra parase(byte[] data){
         return new Extra(data);
    }

   private static int findIndexType(Class cls){
        for(int i=0;i<CLASS_SUPPORTED.length;i++){
            if(cls==CLASS_SUPPORTED[i]){
                return i;
            }
        }
       return -1;
   }

    private static void writeValue(Object value,ObjectOutputStream in)throws IOException{
        Class cls = value.getClass();
        int typeIndex = findIndexType(cls);
        if(typeIndex == -1){
            throw new UnsupportedEncodingException("type error;"+ cls);
        }
        in.writeShort((short)typeIndex);
        if(cls == Integer.class){
            in.writeInt((Integer)value);
        }else  if(cls == byte[].class){
            byte[] data =(byte[])value;
            in.writeInt(data.length);
            in.write(data);
        }else if(cls == String.class){
              in.writeUTF((String)value);
        }else if(cls == Boolean.class){
              in.writeBoolean((Boolean)value);
        }else if(cls == Float.class){
              in.writeFloat((Float)value);
        }else if(cls == Long.class){
              in.writeLong((Long)value);
        }else if(cls == Character.class){
              in.writeChar((Character)value);
        }else if(cls == Short.class){
              in.writeShort((Short)value);
        }else if(cls == Byte.class){
              in.writeByte((Byte)value);
        }else if(cls == Double.class){
              in.writeDouble((Double)value);
        }else{
            throw new UnsupportedEncodingException("type error;"+ cls);
        }
    }

    private static Object readValue(ObjectInputStream in)throws IOException{
        int typeIndex = in.readShort();
        Class cls = CLASS_SUPPORTED[typeIndex];
        if(cls == Integer.class){
              return  in.readInt();
        }else  if(cls == byte[].class){
            int len = in.readInt();
            byte[] data = new byte[len];
            in.read(data);
            return  data;
        }else if(cls == String.class){
            return  in.readUTF();
        }else if(cls == Boolean.class){
            return  in.readBoolean();
        }else if(cls == Float.class){
            return  in.readFloat();
        }else if(cls == Long.class){
            return  in.readLong();
        }else if(cls == Character.class){
            return  in.readChar();
        }else if(cls == Short.class){
            return  in.readShort();
        }else if(cls == Byte.class){
            return  in.readByte();
        }else if(cls == Double.class){
            return  in.readDouble();
        }
        throw new UnsupportedEncodingException("type error;"+ cls);
    }


    private static byte[] save(Map<String,Object> map)throws IOException{
       Set<Map.Entry<String,Object>> entries = map.entrySet();
       ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
       ObjectOutputStream out = new ObjectOutputStream(byteOut);

        for(Map.Entry<String,Object> item:entries){
            out.writeUTF(item.getKey());
            writeValue(item.getValue(),out);
        }
        out.flush();
        byteOut.flush();
        return byteOut.toByteArray();
    }

   private static HashMap<String,Object> load(byte[] data)throws IOException{
       HashMap map = new HashMap<String, Object>();

       if(data!= null && data.length > 0){
           ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
           String key = null ;
           try{
               while( (key = in.readUTF())!=null){
                    Object value = readValue(in);
                   map.put(key,value);
                   key = null;
               }
           }catch(EOFException eof){
                if(key!=null){
                    throw eof;
                }
           }
       }
       return map;
   }

    private void unparcel(){
        if(mParcelData != null){
            synchronized (this){
                if(mParcelData == null){
                    return;
                }
                try {
                    mMap = load(mParcelData);
                } catch (IOException e) {
                    ILog.w(TAG,e.getMessage(),e);
                    mMap = new HashMap<String,Object>();
                }
                mParcelData = null;
            }
        }
    }

    public int size() {
        unparcel();
        return mMap.size();
    }

    /**
     * Returns true if the mapping of this Bundle is empty, false otherwise.
     */
    public boolean isEmpty() {
        unparcel();
        return mMap.isEmpty();
    }

    /**
     * Removes all elements from the mapping of this Bundle.
     */
    public void clear() {
        unparcel();
        mMap.clear();
    }

    public boolean containsKey(String key) {
        unparcel();
        return mMap.containsKey(key);
    }

    /**
     * Returns the entry with the given key as an object.
     *
     * @param key a String key
     * @return an Object, or null
     */
    public Object get(String key) {
        unparcel();
        return mMap.get(key);
    }

    public void remove(String key) {
        unparcel();
        mMap.remove(key);
    }

    public Set<String> keySet() {
        unparcel();
        return mMap.keySet();
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a Boolean, or null
     */
    public  void putBoolean(String key, boolean value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a byte
     */
    public void putByte(String key, byte value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a char, or null
     */
    public   void putChar(String key, char value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a short
     */
    public void putShort(String key, short value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value an int, or null
     */
    public void putInt(String key, int value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a long
     */
    public void putLong(String key, long value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a float
     */
    public void putFloat(String key, float value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a double
     */
    public void putDouble(String key, double value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a String, or null
     */
    public void putString(String key, String value) {
        unparcel();
        mMap.put(key, value);
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a byte array object, or null
     */
    public   void putByteArray(String key, byte[] value) {
        unparcel();
        mMap.put(key, value);
    }



    /**
     * Returns the value associated with the given key, or false if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a boolean value
     */
    public  boolean getBoolean(String key) {
        unparcel();
        return getBoolean(key, false);
    }

    // Log a message if the value was non-null but not of the expected type
    void typeWarning(String key, Object value, String className,
                     Object defaultValue, ClassCastException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key ");
        sb.append(key);
        sb.append(" expected ");
        sb.append(className);
        sb.append(" but value was a ");
        sb.append(value.getClass().getName());
        sb.append(".  The default value ");
        sb.append(defaultValue);
        sb.append(" was returned.");
        ILog.w(TAG, sb.toString());
        ILog.w(TAG, "Attempt to cast generated internal exception:", e);
    }

    void typeWarning(String key, Object value, String className,
                     ClassCastException e) {
        typeWarning(key, value, className, "<null>", e);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Boolean) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Boolean", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (byte) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a byte value
     */
    public byte getByte(String key) {
        unparcel();
        return getByte(key, (byte) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a byte value
     */
    public Byte getByte(String key, byte defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Byte) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Byte", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (char) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a char value
     */
    public  char getChar(String key) {
        unparcel();
        return getChar(key, (char) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a char value
     */
    public char getChar(String key, char defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Character) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Character", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (short) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a short value
     */
    public short getShort(String key) {
        unparcel();
        return getShort(key, (short) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a short value
     */
    public short getShort(String key, short defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Short) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Short", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return an int value
     */
    public int getInt(String key) {
        unparcel();
        return getInt(key, 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return an int value
     */
    public int getInt(String key, int defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Integer) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Integer", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0L if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a long value
     */
    public long getLong(String key) {
        unparcel();
        return getLong(key, 0L);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a long value
     */
    public long getLong(String key, long defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Long) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Long", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0.0f if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a float value
     */
    public float getFloat(String key) {
        unparcel();
        return getFloat(key, 0.0f);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a float value
     */
    public  float getFloat(String key, float defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Float) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Float", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0.0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a double value
     */
    public double getDouble(String key) {
        unparcel();
        return getDouble(key, 0.0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a double value
     */
    public double getDouble(String key, double defaultValue) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return defaultValue;
        }
        try {
            return (Double) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Double", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a String value, or null
     */
    public String getString(String key) {
        unparcel();
        final Object o = mMap.get(key);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "String", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key or if a null
     * value is explicitly associated with the given key.
     *
     * @param key a String, or null
     * @param defaultValue Value to return if key does not exist or if a null
     *     value is associated with the given key.
     * @return the String value associated with the given key, or defaultValue
     *     if no valid String object is currently mapped to that key.
     */
    public String getString(String key, String defaultValue) {
        final String s = getString(key);
        return (s == null) ? defaultValue : s;
    }


    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a byte[] value, or null
     */
    public  byte[] getByteArray(String key) {
        unparcel();
        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (byte[]) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }









}
