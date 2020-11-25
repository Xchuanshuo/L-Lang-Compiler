package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.PeekTokenIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 数组调用表达式
 */
public class ArrayCall extends Expr {

    // exprList的元素个数也表示数组的维度
    private List<Expr> exprList = new ArrayList<>();

    public ArrayCall() {
        super(ASTNodeType.ARRAY_CALL_EXPR);
    }

    public static ArrayCall parse(PeekTokenIterator it, TerminalNode identifier) throws ParseException {
        ArrayCall arrayCall = new ArrayCall();
        arrayCall.remark = "array call";
        arrayCall.addChild(identifier);
        while (it.hasNext() && it.peek().getText().equals("[")) {
            it.nextMatch("[");
            Expr expr = (Expr) Expr.parse(it);
            arrayCall.addChild(expr);
            it.nextMatch("]");
        }
        return arrayCall;
    }

    public TerminalNode identifier() {
        return (TerminalNode) getChild(0);
    }

    public List<Expr> exprList() {
        List<Expr> exprList = getASTNodes(Expr.class);
        if (exprList == null) {
            return null;
        }
        return getASTNodes(Expr.class).subList(1, exprList.size());
    }
}
