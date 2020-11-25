package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.parser.common.PeekTokenIterator;

import static com.legend.lexer.Keyword.Key.THIS;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 函数调用表达式
 */
public class FunctionCall extends Expr {

    public FunctionCall() {
        super(ASTNodeType.FUNCTION_CALL_EXP);
    }

    public static FunctionCall parse(PeekTokenIterator it, TerminalNode identifier) throws ParseException {
        FunctionCall functionCall = new FunctionCall();
        functionCall.setToken(identifier.token);
        functionCall.remark = "function call";
        functionCall.addChild(identifier);
        it.nextMatch("(");
        ExprList exprList = ExprList.parse(it);
        it.nextMatch(")");
        functionCall.addChild(exprList);
        return functionCall;
    }

    public TerminalNode identifier() {
        return (TerminalNode) getChild(0);
    }

    public ExprList exprList() {
        return (ExprList) getChild(1);
    }
}
