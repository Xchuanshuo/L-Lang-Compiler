package com.legend.parser.common;

import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 抽象语法树迭代器
 */
public class ASTIterator {

    public void traverse(ASTListener listener, ASTNode root) {
        root.enter(listener);
        for (ASTNode child : root.getChildren()) {
            traverse(listener, child);
        }
        root.exit(listener);
    }
}
