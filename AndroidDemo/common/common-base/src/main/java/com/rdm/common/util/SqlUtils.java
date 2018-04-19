package com.rdm.common.util;

/**
 * Created by Rao on 2016/10/16.
 */
public class SqlUtils {

    /**
     * sql字符转义
     * @param content
     * @return
     */
    public static String decodeSpecialCharsWhenLikeUseBackslash(String content)
    {
        // 单引号是oracle字符串的边界,oralce中用2个单引号代表1个单引号
        String afterDecode = content.replaceAll("'", "''");
        // 由于使用了/作为ESCAPE的转义特殊字符,所以需要对该字符进行转义
        // 这里的作用是将"a/a"转成"a//a"
        afterDecode = afterDecode.replaceAll("/", "//");
        // 使用转义字符 /,对oracle特殊字符% 进行转义,只作为普通查询字符，不是模糊匹配
        afterDecode = afterDecode.replaceAll("%", "/%");
        // 使用转义字符 /,对oracle特殊字符_ 进行转义,只作为普通查询字符，不是模糊匹配
        afterDecode = afterDecode.replaceAll("_", "/_");
        return afterDecode;
    }
}
