package com.legend.common;

import java.util.regex.Pattern;

/**
 * @author Legend
 * @data by on 20-9-1.
 * @description 字符判断工具类
 */
public class AlphabetHelper {

    private static Pattern ptnLetter = Pattern.compile("^[_a-zA-Z]$");
    private static Pattern ptnSeparator = Pattern.compile("^[{}()\\[\\],.:;]$");
    private static Pattern ptnDigit = Pattern.compile("^[0-9]$");
    private static Pattern ptnLiteral = Pattern.compile("^[_a-zA-Z0-9]$");
    private static Pattern ptnOperator = Pattern.compile("^[*+\\-/%&^|<>=!~]$");
    private static Pattern ptnHexNum = Pattern.compile("^[0-9a-zA-Z]$");
    private static Pattern ptnOctNum = Pattern.compile("^[0-7]$");
    private static Pattern ptnChinese = Pattern.compile("[\\u4E00-\\u9FA5]+");

    public static boolean isLetter(char c) {
        return ptnLetter.matcher(c + "").matches() || isChinese(c);
    }

    public static boolean isDigit(char c) {
        return ptnDigit.matcher(c + "").matches();
    }

    public static boolean isLiteral(char c) {
        return ptnLiteral.matcher(c + "").matches() || isChinese(c);
    }

    public static boolean isOperator(char c) {
        return ptnOperator.matcher(c + "").matches();
    }

    public static boolean isHexNum(char c) {
        return ptnHexNum.matcher(c + "").matches();
    }

    public static boolean isDecNum(char c) {
        return isDigit(c);
    }

    public static boolean isOctNum(char c) {
        return ptnOctNum.matcher(c + "").matches();
    }

    public static boolean isSeparator(char c) {
        return ptnSeparator.matcher(c + "").matches();
    }

    public static boolean isChinese(char c) {
        return ptnChinese.matcher(c + "").matches();
    }
}
