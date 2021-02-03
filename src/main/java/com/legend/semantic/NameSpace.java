package com.legend.semantic;

import com.legend.parser.ast.ASTNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-11-15.
 * @description 命名空间
 */
public class NameSpace extends BlockScope {

    private NameSpace parent = null;
    private List<NameSpace> subNameSpaces = new LinkedList<>();
    private List<Variable> moduleVariables;
    private int moduleVarCount = 0;
    private String name;

    public NameSpace(String name, Scope enclosingScope, ASTNode ast) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.astNode = ast;
    }

    @Override
    public String getName() {
        return name == null ? toString() : name;
    }

    public List<NameSpace> subNameSpaces() {
        return subNameSpaces;
    }

    public void addSubNameSpace(NameSpace child) {
        child.parent = this;
        subNameSpaces.add(child);
    }

    public int getAllSubModuleLocalsSize() {
        int size = 0;
        for (NameSpace space : subNameSpaces) {
            size += space.getLocalsSize();
        }
        return size;
    }

    public void removeSubNameSpace(NameSpace child) {
        child.parent = null;
        subNameSpaces.remove(child);
    }

    private List<Variable> getModuleVariables() {
        if (moduleVariables != null) {
            return moduleVariables;
        }
        moduleVariables = new LinkedList<>();
        for (Symbol symbol : symbols) {
            if (symbol instanceof Variable) {
                symbol.setOffset(moduleVarCount++);
                moduleVariables.add((Variable) symbol);
            }
        }
        return moduleVariables;
    }

    public int getModuleVarCount() {
        return moduleVarCount;
    }

    public Variable findModuleVar(String name) {
        for (Variable variable : getModuleVariables()) {
            if (variable.name.equals(name)) {
                return variable;
            }
        }
        return null;
    }
}
