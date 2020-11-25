package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-15.
 * @description 命名空间
 */
public class NameSpace extends BlockScope {

    private NameSpace parent = null;
    private List<NameSpace> subNameSpaces = new LinkedList<>();
    private String name;

    public NameSpace(String name, Scope enclosingScope, ASTNode ast) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.astNode = ast;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<NameSpace> subNameSpaces() {
        return subNameSpaces;
    }

    public void addSubNameSpace(NameSpace child) {
        child.parent = this;
        subNameSpaces.add(child);
    }

    public void removeSubNameSpace(NameSpace child) {
        child.parent = null;
        subNameSpaces.remove(child);
    }
}
