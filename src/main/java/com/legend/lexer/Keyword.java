package com.legend.lexer;

import java.util.*;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 关键字
 */
public class Keyword {

    public enum Key {
        ABSTRACT, ASSERT, BOOLEAN, BREAK, CONTINUE,
        BYTE, CHAR, CLASS, DEFAULT, DOUBLE, FLOAT,
        SHORT, INT, LONG, STRING, FOR, WHILE, IF, ELSE,
        SWITCH, CASE, RETURN, IS, EXTENDS, AS,
        FINAL, FINALLY, IMPLEMENTS, IMPORT, PRIVATE,
        PROTECTED, PUBLIC, VOID, STATIC, SUPER, THIS,
        THROW, THROWS, TRY, CATCH, FUNCTION,
    }

    private static String[] keyWordStr = {
            "abstract", "assert", "boolean", "break", "continue",
            "byte", "char", "class", "default", "double", "float",
            "short", "int", "long", "string", "for", "while", "if", "else",
            "switch", "case", "return", "is", "extends", "as",
            "final", "finally", "implements", "import", "private",
            "protected", "public", "void", "static", "super", "this",
            "throw", "throws", "try", "catch", "function"
    };

//    private static String[] keyWordStr = {
//            "abstract", "assert", "布尔型", "结束", "continue",
//            "byte", "字符", "类", "default", "double", "浮点数",
//            "short", "整数", "long", "字符串", "f循环", "当", "如果", "否则",
//            "switch", "case", "返回", "is", "继承",
//            "final", "finally", "implements", "import", "private",
//            "protected", "public", "无返回值", "static", "父类", "当前",
//            "throw", "throws", "try", "catch", "函数"
//    };


    private static Map<Key, String> map = new HashMap<>();

    static {
        for (int i = 0;i < Key.values().length;i++) {
            map.put(Key.values()[i], keyWordStr[i]);
        }
    }

    public static String getValueByKey(Key key) {
        return map.get(key);
    }

    public static boolean isKeyword(String str) {
        return map.values().contains(str);
    }
}
