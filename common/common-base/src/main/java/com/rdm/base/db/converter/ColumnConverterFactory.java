package com.rdm.base.db.converter;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.*;

/**
 * Created by hugozhong 2013-11-12
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ColumnConverterFactory {

	private ColumnConverterFactory() {
	}

	public static ColumnConverter getColumnConverter(Class columnType) {
        String columnTypeName = columnType.getName();
		if (sColumnConverterMap.containsKey(columnTypeName)) {
			return sColumnConverterMap.get(columnTypeName);
		}
        ColumnConverter converter = null;
        int size = sInheritConverterClasses.size();
        for (int index = size - 1; index >= 0; index--) {
            Class<?> inheritClass = sInheritConverterClasses.get(index);
            if (inheritClass.isAssignableFrom(columnType)) {
                converter = sColumnConverterMap.get(inheritClass.getName());
                break;
            }
        }
        sColumnConverterMap.put(columnTypeName, converter);
        return converter;
	}

	public static String getDbColumnType(Class columnType) {
		ColumnConverter converter = getColumnConverter(columnType);
		if (converter != null) {
			return converter.getColumnDbType();
		}
		return "TEXT";
	}

	public static boolean isSupportColumnConverter(Class columnType) {
		return getColumnConverter(columnType) != null;
	}

	private static final HashMap<String, ColumnConverter> sColumnConverterMap = new HashMap<String, ColumnConverter>();
    private final static ArrayList<Class<?>> sInheritConverterClasses
            = new ArrayList<Class<?>>();

	static {
		BooleanColumnConverter booleanColumnConverter = new BooleanColumnConverter();
        registerColumnConverter(boolean.class, booleanColumnConverter,false);
        registerColumnConverter(Boolean.class, booleanColumnConverter,false);

		ByteArrayColumnConverter byteArrayColumnConverter = new ByteArrayColumnConverter();
		registerColumnConverter(byte[].class, byteArrayColumnConverter,false);

		ByteColumnConverter byteColumnConverter = new ByteColumnConverter();
		registerColumnConverter(byte.class, byteColumnConverter,false);
		registerColumnConverter(Byte.class, byteColumnConverter,false);

		CharColumnConverter charColumnConverter = new CharColumnConverter();
		registerColumnConverter(char.class, charColumnConverter,false);
		registerColumnConverter(Character.class, charColumnConverter,false);

		DateColumnConverter dateColumnConverter = new DateColumnConverter();
		registerColumnConverter(Date.class, dateColumnConverter,false);

		DoubleColumnConverter doubleColumnConverter = new DoubleColumnConverter();
		registerColumnConverter(double.class, doubleColumnConverter,false);
		registerColumnConverter(Double.class, doubleColumnConverter,false);

		FloatColumnConverter floatColumnConverter = new FloatColumnConverter();
		registerColumnConverter(float.class, floatColumnConverter,false);
		registerColumnConverter(Float.class, floatColumnConverter,false);

		IntegerColumnConverter integerColumnConverter = new IntegerColumnConverter();
		registerColumnConverter(int.class, integerColumnConverter,false);
		registerColumnConverter(Integer.class, integerColumnConverter,false);

		LongColumnConverter longColumnConverter = new LongColumnConverter();
		registerColumnConverter(long.class, longColumnConverter,false);
		registerColumnConverter(Long.class, longColumnConverter,false);

		ShortColumnConverter shortColumnConverter = new ShortColumnConverter();
		registerColumnConverter(short.class, shortColumnConverter,false);
		registerColumnConverter(Short.class, shortColumnConverter,false);

		SqlDateColumnConverter sqlDateColumnConverter = new SqlDateColumnConverter();
		registerColumnConverter(java.sql.Date.class, sqlDateColumnConverter,false);

		StringColumnConverter stringColumnConverter = new StringColumnConverter();
		registerColumnConverter(String.class, stringColumnConverter,false);

		// 必须在最后面
		ParcelColumnConverter parcelColumnConverter = new ParcelColumnConverter();
		registerColumnConverter(Parcelable.class, parcelColumnConverter,true);

		SerializableColumnConverter serializableColumnConverter = new SerializableColumnConverter();
        registerColumnConverter(Serializable.class, serializableColumnConverter,true);
	}

    public static void registerColumnConverter(Class<?> clazz, ColumnConverter converter, boolean allowInherit) {
        if (sColumnConverterMap.containsKey(clazz.getName())) {
            return;
        }
        sColumnConverterMap.put(clazz.getName(), converter);
        if (allowInherit) {
            sInheritConverterClasses.add(clazz);
        }
    }
}
