package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Token;
import com.legend.parser.common.PeekTokenIterator;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 变量初始化
 */
public class VariableInitializer extends ASTNode {

    public VariableInitializer() {
        this.astNodeType = ASTNodeType.VARIABLE_INITIALIZER;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        VariableInitializer variableInitializer = new VariableInitializer();
        Token token = it.peek();
        if (token.getText().equals("{")) {
            ASTNode arrayInitial = ArrayInitializer.parse(it);
            variableInitializer.addChild(arrayInitial);
        } else {
            ASTNode expr = Expr.parse(it);
            variableInitializer.addChild(expr);
        }
        return variableInitializer;
    }

    public ArrayInitializer arrayInitializer() {
        return getASTNode(ArrayInitializer.class);
    }

    public Expr expr() {
        return getASTNode(Expr.class);
    }
}
