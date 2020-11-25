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
    // 符号关联的ast节点
    protected ASTNode astNode;

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

    public void setEnclosingScope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }
}