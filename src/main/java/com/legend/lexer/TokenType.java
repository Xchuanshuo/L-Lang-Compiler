package com.legend.lexer;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description token类型
 */
public enum TokenType {
    KEYWORD, // 关键字
    IDENTIFIER, // 标识符

    // Literals
    DECIMAL_LITERAL, // 十进制数字面量
    HEX_LITERAL, // 16进制数字面量
    OCT_LITERAL, // 8进制数字面量
    BINARY_LITERAL, // 二进制数字面量
    FLOAT_LITERAL, // 浮点数字面量
    BOOL_LITERAL, // 布尔字面量
    CHAR_LITERAL, // 字符字面量
    STRING_LITERAL, // 字符串字面量
    NULL_LITERAL, // null字面量

    // Separators
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    LEFT_BRACE, // [
    RIGHT_BRACE, // ]
    LEFT_BRACK, // {
    RIGHT_BRACK, // }
    SEMICOLON, // ;
    COMMA, // ,
    DOT, // .

    // Operators
    ASSIGN, // =
    GT, // >
    LT, // <
    BANG, // !
    TILDE, // ~
    QUESTION, // ?
    COLON, // :
    EQUAL, // ==
    LE, // <=
    GE, // >=
    NOT_EQUAL, // !=
    LOGIC_AND, // &&
    LOGIC_OR, // ||
    INC, // ++
    DEC, // --
    ADD, // +
    SUB, // -
    MUL, // *
    DIV, // /
    MOD, // %
    BIT_AND, // &
    BIT_OR, // |
    XOR, // ^
    LSHIFT, // <<
    RSHIFT, // >>

    // mixture assign
    ADD_ASSIGN, // +=
    SUB_ASSIGN, // -=
    MUL_ASSIGN, // *=
    DIV_ASSIGN, // /=
    AND_ASSIGN, // &=
    OR_ASSIGN, // |=
    XOR_ASSIGN, // ^=
    MOD_ASSIGN, // %=
    LSHIFT_ASSIGN, // <<=
    RSHIFT_ASSIGN, // >>=
    URSHIFT_ASSIGN, // >>>=


}
