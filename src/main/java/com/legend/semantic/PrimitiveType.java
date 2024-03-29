package com.legend.semantic;

import com.legend.lexer.Keyword;
import com.legend.lexer.Token;

import java.util.Arrays;
import java.util.List;

import static com.legend.lexer.Keyword.Key.*;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 原始类型
 */
public class PrimitiveType implements Type {

    private String name;

    public PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    // 定义程序的基本类型
    public static PrimitiveType Char = new PrimitiveType("C");
    public static PrimitiveType Byte = new PrimitiveType("B");
    public static PrimitiveType Boolean = new PrimitiveType("Z");
    public static PrimitiveType Short = new PrimitiveType("S");
    public static PrimitiveType Integer = new PrimitiveType("I");
    public static PrimitiveType Long = new PrimitiveType("L");
    public static PrimitiveType Float = new PrimitiveType("F");
    public static PrimitiveType Double = new PrimitiveType("D");
    public static PrimitiveType String = new PrimitiveType("ST");
    public static PrimitiveType Null = new PrimitiveType("null");

    /**
     * 计算两个基本类型中比较高的一级, 比如int和long相加, 符号long
     * @param type1
     * @param type2
     * @return
     */
    public static PrimitiveType getUpperType(Type type1, Type type2) {
        PrimitiveType type = null;
        if (type1 == String || type2 == String) {
            type = PrimitiveType.String;
        } else if (type1 == Double || type2 == Double) {
            type = PrimitiveType.Double;
        } else if (type1 == Float || type2 == PrimitiveType.Float) {
            type = PrimitiveType.Float;
        } else if (type1 == Long || type2 == Long) {
            type = PrimitiveType.Long;
        } else if (type1 == Integer || type2 == Integer) {
            type = PrimitiveType.Integer;
        } else if (type1 == Short || type2 == Short) {
            type = PrimitiveType.Short;
        } else {
            type = PrimitiveType.Byte;
        }
        return type;
    }

    public static boolean isNumeric(Type type) {
        if (type == PrimitiveType.Byte ||
                type == PrimitiveType.Short ||
                type == PrimitiveType.Integer ||
                type == PrimitiveType.Long ||
                type == PrimitiveType.Float ||
                type == PrimitiveType.Double) {
            return true;
        } else {
            return false;
        }
    }

    public static Type getBaseTypeByText(String text) {
        Type type = PrimitiveType.Null;
        if (Keyword.isMatchKey(BYTE, text)) {
            type = PrimitiveType.Byte;
        } else if (Keyword.isMatchKey(INT, text)) {
            type = PrimitiveType.Integer;
        } else if (Keyword.isMatchKey(FLOAT, text)) {
            type = PrimitiveType.Float;
        } else if (Keyword.isMatchKey(CHAR, text)) {
            type = PrimitiveType.Char;
        } else if (Keyword.isMatchKey(STRING, text)) {
            type = PrimitiveType.String;
        } else if (Keyword.isMatchKey(BOOLEAN, text)) {
            type = PrimitiveType.Boolean;
        }
        return type;
    }

    public static List<Type> baseTypes() {
        return Arrays.asList(Char, Byte, Short, Integer, Long,
                Float, Double, Boolean, String, Null);
    }

    @Override
    public boolean isType(Type type) {
        return this == type;
    }
}
