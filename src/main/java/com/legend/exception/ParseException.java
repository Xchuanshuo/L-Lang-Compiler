package com.legend.exception;

import com.legend.lexer.Token;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description
 */
public class ParseException extends Exception {

    private String msg;

    public ParseException(Token token) {
        msg = String.format("Syntax Error,  %d line %d column, unexpected token %s",
                token.getLine(), token.getColumn(), token.getText());
    }

    public ParseException(String _msg){
        this.msg = _msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
