package com.legend.semantic.analyze;

import com.legend.parser.Program;
import com.legend.parser.ast.*;
import com.legend.parser.common.BaseASTListener;
import com.legend.semantic.*;
import com.legend.semantic.Class;

import java.util.Objects;
import java.util.Stack;

/**
 * @author Legend
 * @data by on 20-11-15.
 * @description AST第一遍扫描
 *   识别出所有类型(包括类和函数),以及块作用域
 *   函数的参数信息需要在下一阶段添加进去
 */
public class TypeAndScopeScanner extends BaseASTListener {

    private AnnotatedTree at = null;
    private Stack<Scope> stack = new Stack<>();

    public TypeAndScopeScanner(AnnotatedTree at) {
        this.at = at;
    }

    private void pushScope(Scope scope, ASTNode ast) {
        at.scopeOfNode.put(ast, scope);
        stack.push(scope);
        scope.setAstNode(ast);
    }

    private void popScope() {
        stack.pop();
    }

    private Scope currentScope() {
        if (stack.size() > 0) {
            return stack.peek();
        }
        return null;
    }

    @Override
    public void enterProgram(Program program) {
        NameSpace nameSpace = new NameSpace("root", currentScope(), program);
        at.setNameSpace(nameSpace);
        pushScope(nameSpace, program);
    }

    @Override
    public void exitProgram(Program program) {
        popScope();
    }

    @Override
    public void enterBlockStatements(BlockStatements ast) {
        if (ast.getParent() instanceof Program) {
            NameSpace nameSpace = new NameSpace(((Program) ast.getParent()).getModuleId(ast),
                    currentScope(), ast);
            at.getNameSpace().addSubNameSpace(nameSpace);
            pushScope(nameSpace, ast);
        }
    }

    @Override
    public void exitBlockStatements(BlockStatements ast) {
        if (ast.getParent() instanceof Program) {
            popScope();
        }
    }

    @Override
    public void enterBlock(Block ast) {
        if (!(ast.getParent() instanceof FunctionDeclaration)) {
            BlockScope scope = new BlockScope(currentScope(), ast);
            currentScope().addSymbol(scope);
            pushScope(scope, ast);
        }
    }

    @Override
    public void exitBlock(Block ast) {
        if (!(ast.getParent() instanceof FunctionDeclaration)) {
            popScope();
        }
    }

    @Override
    public void enterStatement(Statement ast) {
        // for循环语句需要建立额外的scope
        if (ast.FOR() != null) {
            BlockScope blockScope = new BlockScope(currentScope(), ast);
            Objects.requireNonNull(currentScope()).addSymbol(blockScope);
            pushScope(blockScope, ast);
        }
    }

    @Override
    public void exitStatement(Statement ast) {
        if (ast.FOR() != null) {
            popScope();
        }
    }

    @Override
    public void enterFunctionDeclaration(FunctionDeclaration ast) {
        String name = ast.funcName().getText();
        // 初次扫描函数信息不完整 后续扫描进行补充
        Function function = new Function(name, currentScope(), ast);
        at.types.add(function);
        Objects.requireNonNull(currentScope()).addSymbol(function);
        pushScope(function, ast);
    }

    @Override
    public void exitFunctionDeclaration(FunctionDeclaration ast) {
        popScope();
    }

    @Override
    public void enterClassDeclaration(ClassDeclaration ast) {
        String name = ast.className().getText();
        Class theClass = new Class(name, ast);
        at.types.add(theClass);
        if (at.lookupClass(currentScope(), name) != null) {
            at.log("Duplicated class defined", ast);
        }
        currentScope().addSymbol(theClass);
        pushScope(theClass, ast);
    }

    @Override
    public void exitClassDeclaration(ClassDeclaration ast) {
        popScope();
    }
}
