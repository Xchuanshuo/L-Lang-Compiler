package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 变量声明
 */
public class VariableDeclarator extends ASTNode {

    public VariableDeclarator() {
        this.astNodeType = ASTNodeType.VARIABLE_DECLARATION;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.addChild(new TerminalNode(it.nextMatch(TokenType.IDENTIFIER)));
        if (it.hasNext()) {
            String text = it.peek().getText();
//            if (!text.equals("=") && !text.equals(";") && !text.equals(",")) {
//                throw new ParseException(it.peek());
//            }
            if (text.equals("=")) {
                it.nextMatch(TokenType.ASSIGN);
                ASTNode variableInitializer = VariableInitializer.parse(it);
                variableDeclarator.addChild(variableInitializer);
            }
        }
        return variableDeclarator;
    }

    public TerminalNode identifier() {
        return getTerminalNode(TokenType.IDENTIFIER);
    }

    public VariableInitializer variableInitializer() {
        return getASTNode(VariableInitializer.class);
    }

}
