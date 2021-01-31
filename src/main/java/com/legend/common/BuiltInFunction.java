package com.legend.common;

import com.legend.semantic.PrimitiveType;
import com.legend.semantic.Type;
import com.sun.org.apache.regexp.internal.RE;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Legend
 * @data by on 20-11-20.
 * @description 用来保存内建函数
 */
public class BuiltInFunction {

    public enum BuiltIn {
        PRINT, PRINTLN, READ, WRITE,
        INT, STR, FLOAT, BYTE, CHAR,
        STR_LEN,STR_AT,
    }

    private static String[] builtInStr_en = {
            "print", "println", "read", "write",
            "int", "str", "float", "byte", "char",
            "strLen", "strAt",
    };

    private static String[] builtInStr_zh = {
            "打印", "换行打印", "read", "write",
            "整形", "字符串", "浮点数", "字节", "字符型",
            "字符串长度", "获取字符",
    };


//    private static Map<BuiltIn, String> map = new HashMap<>();
    private static Map<BuiltIn, String[]> map = new HashMap<>();

    static {
        for (int i = 0;i < BuiltIn.values().length;i++) {
            map.put(BuiltIn.values()[i], new String[]{builtInStr_en[i], builtInStr_zh[i]});
        }
    }

    public static BuiltIn getKeyByType(Type type) {
        if (type == PrimitiveType.Integer) {
            return BuiltIn.INT;
        } else if (type == PrimitiveType.Float) {
            return BuiltIn.FLOAT;
        } else if (type == PrimitiveType.Byte) {
            return BuiltIn.BYTE;
        } else if (type == PrimitiveType.String) {
            return BuiltIn.STR;
        }
        return null;
    }

    public static String getValueByKey(BuiltIn key) {
        return map.get(key)[0];
    }

    public static boolean isMatchKey(BuiltIn key, String str) {
        String[] keys = map.get(key);
        for (String k : keys) {
            if (k.equals(str)) return true;
        }
        return false;
    }

    public static boolean isBuiltInFunc(String str) {
        Collection<String[]> values = map.values();
        for (String[] value : values) {
            for (String v : value) {
                if (v.equals(str)) return true;
            }
        }
        return false;
    }
}
