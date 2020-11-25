package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.PeekTokenIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 表达式列表
 */
public class ExprList extends ASTNode {

    private List<Expr> exprList = new ArrayList<>();

    public ExprList() {
        this.astNodeType = ASTNodeType.EXP_LIST;
        this.remark = "expr list";
    }

    public static ExprList parse(PeekTokenIterator it) throws ParseException {
        ExprList exprList = new ExprList();
        ASTNode expr;
        while ((expr = Expr.parse(it)) != null) {
            exprList.addChild(expr);
            if (!it.peek().getText().equals(")")) {
                it.nextMatch(",");
            }
        }
        return exprList;
    }

    public List<Expr> exprList() {
        if (exprList.size() != getChildren().size()) {
            for (ASTNode node : getChildren()) {
                exprList.add((Expr) node);
            }
        }
        return exprList;
    }

}
