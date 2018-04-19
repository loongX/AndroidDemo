package com.rdm.common.util;

import com.rdm.common.ILog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Rao on 2015/1/11.
 */
public class GenericUtils {

    public static Class getSuperClassGenricType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    public static Class getGenericInterfaces(Class clazz) {
        return getGenericInterfaces(clazz, 0);
    }

    public static Class getGenericInterfaces(Class clazz, int index) {
        Type[] params = clazz.getGenericInterfaces();


        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            ILog.w("GenericUtils", clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];

    }

        /**
         * 茅&#x20AC;&#x161;??&#x2021;????掳&#x201E;,?&#x17D;??聻&#x2014;?聨&#x161;盲職&#x2030;Class?&#x2014;???掳?&#x2DC;&#x17D;莽&#x161;&#x201E;莽&#x2C6;?莽??莽&#x161;&#x201E;?&#x152;&#x192;?&#x17E;&#x2039;??&#x201A;?&#x2022;掳莽&#x161;&#x201E;莽???&#x17E;&#x2039;. ??&#x201A;public BookManager extends GenricManager<Book>
         *
         * @param clazz clazz The class to introspect
         * @param index the Index of the generic ddeclaration,start from 0.
         * @return the index generic declaration, or <code>Object.class</code> if cannot be determined
         */
    public static Class getSuperClassGenricType(Class clazz, int index) {

        Type genType = clazz.getGenericSuperclass();


        if (!(genType instanceof ParameterizedType)) {
            ILog.w("GenericUtils",clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            ILog.w("GenericUtils", "Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            ILog.w("GenericUtils", clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }


}
