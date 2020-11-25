package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.PeekTokenIterator;
import com.legend.parser.common.PeekUtils;

/**
 * @author Legend
 * @data by on 20-11-12.
 * @description for循环控制块
 */
public class ForControl extends ASTNode {

    public ForControl() {
        this.astNodeType = ASTNodeType.FOR_CONTROL;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ForControl forControl = new ForControl();
        ASTNode forInit = ForInit.parse(it);
        if (forInit != null) {
            forControl.addChild(forInit);
        }
        it.nextMatch(";");
        ASTNode expr = Expr.parse(it);
        if (expr != null) {
            forControl.addChild(expr);
        }
        it.nextMatch(";");
        ASTNode exprList = ExprList.parse(it);
        if (exprList != null) {
            forControl.addChild(exprList);
        }
        return forControl;
    }

    public ForInit forInit() {
        return getASTNode(ForInit.class);
    }

    public Expr expr() {
        return getASTNode(Expr.class);
    }

    public ExprList forUpdate() {
        return getASTNode(ExprList.class);
    }

    static public class ForInit extends ASTNode {

        public ForInit() {
            this.astNodeType = ASTNodeType.FOR_INIT;
        }

        public static ASTNode parse(PeekTokenIterator it) throws ParseException {
            ForInit forInit = new ForInit();
            if (PeekUtils.isVariableDeclaration(it)) {
                forInit.addChild(VariableDeclarators.parse(it));
            } else {
                ExprList exprList = ExprList.parse(it);
                if (exprList.exprList().size() > 0) {
                    forInit.addChild(exprList);
                }
            }
            return forInit;
        }

        public VariableDeclarators variableDeclarators() {
            return getASTNode(VariableDeclarators.class);
        }

        public ExprList exprList() {
            return getASTNode(ExprList.class);
        }
    }
}
