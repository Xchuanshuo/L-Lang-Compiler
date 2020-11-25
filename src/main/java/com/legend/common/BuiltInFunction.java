package com.legend.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-11-20.
 * @description 用来保存内建函数
 */
public class BuiltInFunction {

    public enum BuiltIn {
        PRINT, PRINTLN, READ, WRITE,
        INT, STR, FLOAT, BYTE, CHAR,
        STR_LEN,STR_AT,STR_REPLACE
    }

    private static String[] builtInStr = {
            "print", "println", "read", "write",
            "int", "str", "float", "byte", "char",
            "strLen", "strAt", "strReplace"
    };

//    private static String[] builtInStr = {
//            "打印", "换行打印", "read", "write",
//    };


    private static Map<BuiltIn, String> map = new HashMap<>();

    static {
        for (int i = 0;i < BuiltIn.values().length;i++) {
            map.put(BuiltIn.values()[i], builtInStr[i]);
        }
    }

    public static String getValueByKey(BuiltIn key) {
        return map.get(key);
    }

    public static boolean isBuiltInFunc(String str) {
        return map.values().contains(str);
    }
}
