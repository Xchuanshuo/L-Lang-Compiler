package com.legend.lexer;

import java.util.*;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 关键字
 */
public class Keyword {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public enum Key {
        ABSTRACT, ASSERT, BOOLEAN, BREAK, CONTINUE,
        BYTE, CHAR, CLASS, DEFAULT, DOUBLE, FLOAT,
        SHORT, INT, LONG, STRING, FOR, WHILE, IF, ELSE,
        SWITCH, CASE, RETURN, IS, EXTENDS, AS,
        FINAL, FINALLY, IMPLEMENTS, IMPORT, PRIVATE,
        PROTECTED, PUBLIC, VOID, STATIC, SUPER, THIS,
        THROW, THROWS, TRY, CATCH, FUNCTION, BUILTIN
    }

    private static String[] keyWordStr_en = {
            "abstract", "assert", "boolean", "break", "continue",
            "byte", "char", "class", "default", "double", "float",
            "short", "int", "long", "string", "for", "while", "if", "else",
            "switch", "case", "return", "is", "extends", "as",
            "final", "finally", "implements", "import", "private",
            "protected", "public", "void", "static", "super", "this",
            "throw", "throws", "try", "catch", "function", "builtin"
    };

    private static String[] keyWordStr_zh = {
            "abstract", "assert", "布尔型", "结束", "continue",
            "byte", "字符", "类", "default", "double", "浮点数",
            "short", "整数", "long", "字符串", "f循环", "当", "如果", "否则",
            "switch", "case", "返回", "is", "继承", "作为",
            "final", "finally", "implements", "import", "private",
            "protected", "public", "无返回值", "静态", "父类", "当前",
            "throw", "throws", "try", "catch", "函数", "内建"
    };

//    private static Map<Key, String> map = new HashMap<>();
    private static Map<Key, String[]> map = new HashMap<>();

    static {
        for (int i = 0;i < Key.values().length;i++) {
            map.put(Key.values()[i], new String[]{keyWordStr_en[i], keyWordStr_zh[i]});
        }
    }

    public static String getValueByKey(Key key) {
        return map.get(key)[0];
    }

    public static String[] getValuesByKey(Key key) {
        return map.get(key);
    }

    public static boolean isMatchKey(Key k, String t) {
        String[] keys = map.get(k);
        for (String key : keys) {
            if (key.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKeyword(String str) {
        Collection<String[]> values = map.values();
        for (String[] value : values) {
            for (String v: value) {
                if (v.equals(str)) return true;
            }
        }
        return false;
    }

    public static boolean isBoolLiteral(String val) {
        return val.equals(TRUE) || val.equals(FALSE);
    }
}
