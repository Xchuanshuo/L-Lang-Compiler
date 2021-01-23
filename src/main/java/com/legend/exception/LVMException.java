package com.legend.exception;

import com.legend.lexer.Token;
import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 21-1-23.
 * @description
 */
public class LVMException  extends RuntimeException {

    private String msg;

    public LVMException(String _msg) {
        this.msg = _msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}