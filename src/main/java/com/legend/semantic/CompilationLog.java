package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-15.
 * @description 记录编译过程中产生的信息
 */
public class CompilationLog {

    protected String message = null;
    protected int line, column;

    // 相关的ast节点
    protected ASTNode ast;
    // log的类型, 包括信息、警告、错误
    protected int type = INFO;

    public static int INFO = 0;
    public static int WARNING = 0;
    public static int ERROR = 0;

    @Override
    public String toString() {
        return message + " @" + line + ":" + column;
    }
}
