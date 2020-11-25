package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 类或接口类型
 */
public class ClassOrInterfaceType extends ASTNode {

    public ClassOrInterfaceType() {
        this.astNodeType = ASTNodeType.CLASS_OR_INTERFACE_TYPE;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType();
        TerminalNode t = new TerminalNode(it.nextMatch(TokenType.IDENTIFIER));
        classOrInterfaceType.addChild(t);
        while (it.hasNext() && it.peek().getTokenType().equals(TokenType.DOT)) {
            it.nextMatch(TokenType.DOT);
            TerminalNode terminalNode = new TerminalNode(
                    it.nextMatch(TokenType.IDENTIFIER));
            classOrInterfaceType.addChild(terminalNode);
        }
        return classOrInterfaceType;
    }

    public List<TerminalNode> identifiers() {
        return getASTNodes(TerminalNode.class);
    }
}
