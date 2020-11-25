package com.legend.parser.ast;

import com.legend.lexer.Token;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 终端节点
 */
public class TerminalNode extends Expr {

    public TerminalNode(Token token) {
        this.token = token;
        this.astNodeType = ASTNodeType.TERMINAL_NODE;
    }

    @Override
    public String getText() {
        return token.getText();
    }
}
