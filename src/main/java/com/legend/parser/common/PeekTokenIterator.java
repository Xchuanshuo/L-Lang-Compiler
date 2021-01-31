package com.legend.parser.common;

import com.legend.common.PeekIterator;
import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description token迭代器
 */
public class PeekTokenIterator extends PeekIterator<Token> {

    public PeekTokenIterator(Token[] array) {
        super(array);
    }

    public PeekTokenIterator(Token[] array, Token endToken) {
        super(array, endToken);
    }

    public PeekTokenIterator(List<Token> list) {
        super(list);
    }

    public PeekTokenIterator(List<Token> list, Token endToken) {
        super(list, endToken);
    }

    public PeekTokenIterator(Stream<Token> stream) {
        super(stream);
    }

    public PeekTokenIterator(Stream<Token> stream, Token endToken) {
        super(stream, endToken);
    }

    public PeekTokenIterator(Iterator<Token> it, Token endToken) {
        super(it, endToken);
    }

    public Token nextMatch(String text) throws ParseException {
        Token token = next();
        if (!token.getText().equals(text)) {
            throw new ParseException(token);
        }
        return token;
    }

    public Token nextMatch(TokenType type) throws ParseException {
        Token token = next();
        if (!token.getTokenType().equals(type)) {
            throw new ParseException(token);
        }
        return token;
    }

    public Token nextMatch(Keyword.Key key) throws ParseException {
        Token token = next();
        if (!Keyword.isMatchKey(key, token.getText())) {
            throw new ParseException(token);
        }
        return token;
    }

    public boolean topIsEqual(TokenType type) {
        if (!hasNext()) return false;
        return peek().getTokenType() == type;
    }

    public boolean topIsEqual(String text) {
        if (!hasNext()) return false;
        return peek().getText().equals(text);
    }

    public boolean topIsEqual(Keyword.Key key) {
        if (!hasNext()) return false;
        return Keyword.isMatchKey(key, peek().getText());
    }

    public Token lookahead() {
        if (hasNext()) {
            next();
            Token token = peek();
            putBack();
            return token;
        }
        return null;
    }

    public boolean aheadIsEqual(TokenType type) {
        Token token = lookahead();
        if (token == null) return false;
        return peek().getTokenType() == type;
    }

    public boolean aheadIsEqual(String text) {
        Token token = lookahead();
        if (token == null) return false;
        return peek().getText().equals(text);
    }

    public boolean aheadIsEqual(Keyword.Key key) {
        Token token = lookahead();
        if (token == null) return false;
        return Keyword.isMatchKey(key, peek().getText());
    }

}
