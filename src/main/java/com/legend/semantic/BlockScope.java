package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-15.
 * @description 块作用域
 */
public class BlockScope extends Scope {

    private static int index = 0;

    public BlockScope() {
        this.name = "block" + index++;
    }

    public BlockScope(Scope enclosingScope, ASTNode ast) {
        this.name = "block" + index++;
        this.enclosingScope = enclosingScope;
        this.astNode = ast;
    }

    @Override
    public String toString() {
        return "Block " + name;
    }
}
