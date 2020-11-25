package com.legend.parser.common;

import com.legend.parser.Program;
import com.legend.parser.ast.BlockStatement;
import com.legend.parser.ast.BlockStatements;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 访问者模式测试
 */
public class MyASTVisitor extends BaseASTVisitor<Object> {

    @Override
    public Object visitProgram(Program ast) {
        Object obj = null;
        BlockStatements blockStatements = ast.blockStatements();
        if (blockStatements != null) {
            for (BlockStatement blockStatement : blockStatements.blockStatements()) {
                obj = visitBlockStatement(blockStatement);
            }
        }
        return obj;
    }

    @Override
    public Object visitBlockStatement(BlockStatement ast) {
        System.out.println("访问: ");
        ast.dumpAST();
        return super.visitBlockStatement(ast);
    }
}
