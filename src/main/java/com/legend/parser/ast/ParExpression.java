package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.PeekTokenIterator;

/**
 * @author Legend
 * @data by on 20-11-12.
 * @description 括号表达式
 */
public class ParExpression extends ASTNode {

    public ParExpression() {
        this.astNodeType = ASTNodeType.PAR_EXPRESSION;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ParExpression parExpression = new ParExpression();
        it.nextMatch("(");
        ASTNode expr = Expr.parse(it);
        parExpression.addChild(expr);
        it.nextMatch(")");
        return parExpression;
    }

    public Expr expr() {
        return getASTNode(Expr.class);
    }
}
