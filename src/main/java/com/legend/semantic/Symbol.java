package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 符号
 */
public class Symbol {

    // 符号名称
    protected String name;
    // 所属作用域
    protected Scope enclosingScope;
    // 符号可见性(private、public)
    private int visibility;
    // 符号是否为静态
    private boolean isStatic = false;
    // 符号关联的ast节点
    protected ASTNode astNode;

    private int offset = 0;

    public String getName() {
        return name;
    }

    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setEnclosingScope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
