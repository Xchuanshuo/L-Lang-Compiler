package com.legend.parser.common;

import com.legend.parser.Program;
import com.legend.parser.ast.*;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description
 */
public class BaseASTVisitor<T> implements ASTVisitor<T> {

    @Override
    public T visitProgram(Program ast) {
        return null;
    }

    @Override
    public T visitArrayInitializer(ArrayInitializer ast) {
        return null;
    }

    @Override
    public T visitArrayCall(ArrayCall ast) {
        return null;
    }

    @Override
    public T visitBlock(Block ast) {
        return null;
    }

    @Override
    public T visitBlockStatement(BlockStatement ast) {
        return null;
    }

    @Override
    public T visitBlockStatements(BlockStatements ast) {
        return null;
    }

    @Override
    public T visitClassDeclaration(ClassDeclaration ast) {
        return null;
    }

    @Override
    public T visitClassBody(ClassDeclaration.ClassBody ast) {
        return null;
    }

    @Override
    public T visitMemberDeclaration(ClassDeclaration.MemberDeclaration ast) {
        return null;
    }

    @Override
    public T visitClassOrInterfaceType(ClassOrInterfaceType ast) {
        return null;
    }

    @Override
    public T visitExpr(Expr ast) {
        return null;
    }

    @Override
    public T visitExprList(ExprList ast) {
        return null;
    }

    @Override
    public T visitForControl(ForControl ast) {
        return null;
    }

    @Override
    public T visitForInit(ForControl.ForInit forInit) {
        return null;
    }

    @Override
    public T visitFunctionCall(FunctionCall ast) {
        return null;
    }

    @Override
    public T visitFunctionDeclaration(FunctionDeclaration ast) {
        return null;
    }

    @Override
    public T visitFormalParameterList(FunctionDeclaration.FormalParameterList ast) {
        return null;
    }

    @Override
    public T visitFormalParameter(FunctionDeclaration.FormalParameter ast) {
        return null;
    }

    @Override
    public T visitFunctionType(FunctionType ast) {
        return null;
    }

    @Override
    public T visitLiteral(Literal ast) {
        return null;
    }

    @Override
    public T visitParExpression(ParExpression ast) {
        return null;
    }

    @Override
    public T visitStatement(Statement ast) {
        return null;
    }

    @Override
    public T visitTerminalNode(TerminalNode ast) {
        return null;
    }

    @Override
    public T visitTypeList(TypeList ast) {
        return null;
    }

    @Override
    public T visitTypeType(TypeType ast) {
        return null;
    }

    @Override
    public T visitTypeTypeOrVoid(TypeTypeOrVoid ast) {
        return null;
    }

    @Override
    public T visitVariableDeclarator(VariableDeclarator ast) {
        return null;
    }

    @Override
    public T visitVariableDeclarators(VariableDeclarators ast) {
        return null;
    }

    @Override
    public T visitVariableInitializer(VariableInitializer ast) {
        return null;
    }
}
