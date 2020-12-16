package com.legend.lexer;

import static com.legend.lexer.Keyword.Key.*;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description token
 */
public class Token {

    private TokenType tokenType;
    private String text;
    private int line;
    private int column;

    public Token(TokenType type, String text) {
        this.tokenType = type;
        this.text = text;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isId() {
        return tokenType == TokenType.IDENTIFIER;
    }

    public boolean isKeyword() {
        return tokenType == TokenType.KEYWORD;
    }

    public boolean isBaseType() {
        if (tokenType != TokenType.KEYWORD) {
            return false;
        }
        return getText().equals(Keyword.getValueByKey(INT)) ||
                getText().equals(Keyword.getValueByKey(FLOAT)) ||
                getText().equals(Keyword.getValueByKey(CHAR)) ||
                getText().equals(Keyword.getValueByKey(STRING)) ||
                getText().equals(Keyword.getValueByKey(BOOLEAN));
    }

    public boolean isThisOrSuper() {
        if (tokenType != TokenType.KEYWORD) {
            return false;
        }
        return getText().equals(Keyword.getValueByKey(THIS)) ||
                getText().equals(Keyword.getValueByKey(SUPER));
    }

    public boolean isLiteral() {
        return tokenType == TokenType.STRING_LITERAL ||
                tokenType == TokenType.FLOAT_LITERAL ||
                tokenType == TokenType.BOOL_LITERAL ||
                tokenType == TokenType.DECIMAL_LITERAL ||
                tokenType == TokenType.OCT_LITERAL ||
                tokenType == TokenType.HEX_LITERAL ||
                tokenType == TokenType.NULL_LITERAL ||
                tokenType == TokenType.CHAR_LITERAL;
    }

    public boolean isAssignOperator() {
        return tokenType == TokenType.ASSIGN ||
                tokenType == TokenType.ADD_ASSIGN ||
                tokenType == TokenType.SUB_ASSIGN ||
                tokenType == TokenType.MUL_ASSIGN ||
                tokenType == TokenType.DIV_ASSIGN ||
                tokenType == TokenType.MOD_ASSIGN ||
                tokenType == TokenType.LSHIFT_ASSIGN ||
                tokenType == TokenType.RSHIFT_ASSIGN ||
                tokenType == TokenType.OR_ASSIGN ||
                tokenType == TokenType.XOR_ASSIGN ||
                tokenType == TokenType.AND_ASSIGN;
    }

    public boolean isValue() {
        return isLiteral() || isId();
    }

    @Override
    public String toString() {
        return String.format("%sL:%s [%s] @ %s",
                line, column, tokenType, text);
    }
}
