package com.legend.semantic.analyze;

import com.legend.parser.ast.ASTNode;
import com.legend.semantic.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Legend
 * @data by on 20-11-18.
 * @description 闭包分析器
 */
public class ClosureAnalyzer {

    private AnnotatedTree at;

    public ClosureAnalyzer(AnnotatedTree at) {
        this.at = at;
    }

    public void analyzeClosure() {
        for (Type type : at.types) {
            if (type instanceof Function && !((Function) type).isMethod()) {
                Set<Variable> variables = calculateClosureVariables((Function)type);
                if (variables.size() > 0) {
                    ((Function) type).setClosureVariables(variables);
                }
            }
        }
    }

    // 计算函数的闭包(自由)变量
    // 1.分别计算函数内所引用与声明的所有变量 2.从引用变量从移除声明的变量
    private Set<Variable> calculateClosureVariables(Function function) {
        Set<Variable> referenced = variableReferencedByScope(function);
        Set<Variable> declares = variableDeclareUnderScope(function);
        referenced.removeAll(declares);
        return referenced;
    }

    private Set<Variable> variableDeclareUnderScope(Scope scope) {
        Set<Variable> set = new HashSet<>();
        for (Symbol symbol : scope.getSymbols()) {
            if (symbol instanceof Variable) {
                set.add((Variable) symbol);
                ((Variable) symbol).setUpValue(false);
            } else if (symbol instanceof Scope) {
                set.addAll(variableDeclareUnderScope((Scope) symbol));
            }
        }
        return set;
    }

    private Set<Variable> variableReferencedByScope(Scope scope) {
        Set<Variable> set = new HashSet<>();
        ASTNode ast = scope.getAstNode();
        for (ASTNode node: at.symbolOfNode.keySet()) {
            Symbol symbol = at.symbolOfNode.get(node);
            if (symbol instanceof Variable && !((Variable) symbol).isModuleVar()
                    && isAncestor(ast, node)) {
                ((Variable) symbol).setUpValue(true);
                set.add((Variable) symbol);
            }
        }
        return set;
    }

    /**
     * 判断节点1是否是节点2的祖先
     * @param node1 节点1
     * @param node2 节点2
     * @return 判断结果
     */
    private boolean isAncestor(ASTNode node1, ASTNode node2) {
        if (node2.getParent() == null) {
            return false;
        } else if (node2.getParent() == node1) {
            return true;
        }
        return isAncestor(node1, node2.getParent());
    }

}
