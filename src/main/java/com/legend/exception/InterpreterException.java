package com.legend.exception;

import com.legend.lexer.Token;
import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-23.
 * @description 解释器异常
 */
public class InterpreterException extends RuntimeException {

    private String msg;

    public InterpreterException(ASTNode ast) {
        Token token = ast.getToken();
        msg = String.format("Execute Error,  %d line %d column, unexpected syntax %s",
                token.getLine(), token.getColumn(), token.getText());
    }

    public InterpreterException(String _msg){
        this.msg = _msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
