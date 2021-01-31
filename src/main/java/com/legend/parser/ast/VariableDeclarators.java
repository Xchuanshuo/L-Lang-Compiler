package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 变量声明器
 */
public class VariableDeclarators extends ASTNode {

    public VariableDeclarators() {
        this.astNodeType = ASTNodeType.VARIABLE_DECLARATORS;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        VariableDeclarators declarators = new VariableDeclarators();
        if (it.topIsEqual(Keyword.Key.STATIC)) {
            declarators.addChild(new TerminalNode(it.nextMatch(Keyword.Key.STATIC)));
        }
        ASTNode typeNode = TypeType.parse(it);
        declarators.addChild(typeNode);
        ASTNode declarator = VariableDeclarator.parse(it);
        declarators.addChild(declarator);
        while (it.hasNext() && it.peek().getText().equals(",")) {
            it.next();
            ASTNode child = VariableDeclarator.parse(it);
            declarators.addChild(child);
        }
        return declarators;
    }

    public TerminalNode STATIC() {
        return getTerminalNode(Keyword.Key.STATIC);
    }

    public TypeType typeType() {
        return getASTNode(TypeType.class);
    }

    public List<VariableDeclarator> variableDeclaratorList() {
        return getASTNodes(VariableDeclarator.class);
    }
}
