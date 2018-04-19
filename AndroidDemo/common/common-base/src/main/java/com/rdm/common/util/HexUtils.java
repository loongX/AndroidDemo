/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rdm.common.util;

import java.io.IOException;
import java.io.OutputStream;

import java.io.UnsupportedEncodingException;

public class HexUtils {
    private static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final byte[] emptybytes = new byte[0];

    public HexUtils() {
    }

    public static String byte2HexStr(byte b) {
        char[] buf = new char[]{'\u0000', digits[b & 15]};
        b = (byte)(b >>> 4);
        buf[0] = digits[b & 15];
        return new String(buf);
    }

    public static String bytes2HexStr(byte[] bytes) {
        if(bytes != null && bytes.length != 0) {
            char[] buf = new char[2 * bytes.length];

            for(int i = 0; i < bytes.length; ++i) {
                byte b = bytes[i];
                buf[2 * i + 1] = digits[b & 15];
                b = (byte)(b >>> 4);
                buf[2 * i + 0] = digits[b & 15];
            }

            return new String(buf);
        } else {
            return null;
        }
    }

    public static String bytes2HexStr(byte[] bytes, int offset, int count) {
        if(bytes != null && bytes.length != 0) {
            char[] buf = new char[2 * count];
            int size = offset + count;

            for(int i = offset; i < size; ++i) {
                byte b = bytes[i];
                buf[2 * i + 1] = digits[b & 15];
                b = (byte)(b >>> 4);
                buf[2 * i + 0] = digits[b & 15];
            }

            return new String(buf);
        } else {
            return null;
        }
    }

    public static byte hexStr2Byte(String str) {
        return str != null && str.length() == 1?char2Byte(str.charAt(0)):0;
    }

    public static byte char2Byte(char ch) {
        return ch >= 48 && ch <= 57?(byte)(ch - 48):(ch >= 97 && ch <= 102?(byte)(ch - 97 + 10):(ch >= 65 && ch <= 70?(byte)(ch - 65 + 10):0));
    }

    public static byte[] hexStr2Bytes(String str) {
        if(str != null && !str.equals("")) {
            byte[] bytes = new byte[str.length() / 2];

            for(int i = 0; i < bytes.length; ++i) {
                char high = str.charAt(i * 2);
                char low = str.charAt(i * 2 + 1);
                bytes[i] = (byte)(char2Byte(high) * 16 + char2Byte(low));
            }

            return bytes;
        } else {
            return emptybytes;
        }
    }

    public static void main(String[] args) {
        try {
            byte[] bytes = "Hello WebSocket World?".getBytes("gbk");
            System.out.println(bytes2HexStr(bytes));
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        }

    }
}