package com.legend.parser.common;

import com.legend.parser.Program;
import com.legend.parser.ast.*;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 抽象语法树监听器默认实现, 子类根据需要重写对应的方法
 */
public class BaseASTListener implements ASTListener {

    @Override
    public void enterProgram(Program ast) { }

    @Override
    public void exitProgram(Program ast) { }

    @Override
    public void enterClassDeclaration(ClassDeclaration ast) {

    }

    @Override
    public void exitClassDeclaration(ClassDeclaration ast) {

    }

    @Override
    public void enterClassBody(ClassDeclaration.ClassBody ast) {

    }

    @Override
    public void exitClassBody(ClassDeclaration.ClassBody ast) {

    }

    @Override
    public void enterMemberDeclaration(ClassDeclaration.MemberDeclaration ast) {

    }

    @Override
    public void exitMemberDeclaration(ClassDeclaration.MemberDeclaration ast) {

    }

    @Override
    public void enterFunctionDeclaration(FunctionDeclaration ast) {

    }

    @Override
    public void exitFunctionDeclaration(FunctionDeclaration ast) {

    }

    @Override
    public void enterFormalParameterList(FunctionDeclaration.FormalParameterList ast) {

    }

    @Override
    public void exitFormalParameterList(FunctionDeclaration.FormalParameterList ast) {

    }

    @Override
    public void enterFormalParameter(FunctionDeclaration.FormalParameter ast) {

    }

    @Override
    public void exitFormalParameter(FunctionDeclaration.FormalParameter ast) {

    }

    @Override
    public void enterArrayCall(ArrayCall ast) {

    }

    @Override
    public void exitArrayCall(ArrayCall ast) {

    }

    @Override
    public void enterArrayInitializer(ArrayInitializer ast) {

    }

    @Override
    public void exitArrayInitializer(ArrayInitializer ast) {

    }

    @Override
    public void enterBlock(Block ast) {

    }

    @Override
    public void exitBlock(Block ast) {

    }

    @Override
    public void enterBlockStatement(BlockStatement ast) {

    }

    @Override
    public void exitBlockStatement(BlockStatement ast) {

    }

    @Override
    public void enterBlockStatements(BlockStatements ast) {

    }

    @Override
    public void exitBlockStatements(BlockStatements ast) {

    }

    @Override
    public void enterClassOrInterfaceType(ClassOrInterfaceType ast) {

    }

    @Override
    public void exitClassOrInterfaceType(ClassOrInterfaceType ast) {

    }

    @Override
    public void enterExpr(Expr ast) {

    }

    @Override
    public void exitExpr(Expr ast) {

    }

    @Override
    public void enterExprList(ExprList ast) {

    }

    @Override
    public void exitExprList(ExprList ast) {

    }

    @Override
    public void enterForControl(ForControl ast) {

    }

    @Override
    public void exitForControl(ForControl ast) {

    }

    @Override
    public void enterForInit(ForControl.ForInit ast) {

    }

    @Override
    public void exitForInit(ForControl.ForInit ast) {

    }

    @Override
    public void enterFunctionCall(FunctionCall ast) {

    }

    @Override
    public void exitFunctionCall(FunctionCall ast) {

    }

    @Override
    public void enterFunctionType(FunctionType ast) {

    }

    @Override
    public void exitFunctionType(FunctionType ast) {

    }

    @Override
    public void enterLiteral(Literal ast) {

    }

    @Override
    public void exitLiteral(Literal ast) {

    }

    @Override
    public void enterParExpression(ParExpression ast) {

    }

    @Override
    public void exitParExpression(ParExpression ast) {

    }

    @Override
    public void enterPrimitiveType(PrimitiveType ast) {

    }

    @Override
    public void exitPrimitiveType(PrimitiveType ast) {

    }

    @Override
    public void enterStatement(Statement ast) {

    }

    @Override
    public void exitStatement(Statement ast) {

    }

    @Override
    public void enterTypeList(TypeList ast) {

    }

    @Override
    public void exitTypeList(TypeList ast) {

    }

    @Override
    public void enterTypeType(TypeType ast) {

    }

    @Override
    public void exitTypeType(TypeType ast) {

    }

    @Override
    public void enterTypeTypeOrVoid(TypeTypeOrVoid ast) {

    }

    @Override
    public void exitTypeTypeOrVoid(TypeTypeOrVoid ast) {

    }

    @Override
    public void enterVariableDeclarators(VariableDeclarators ast) {

    }

    @Override
    public void exitVariableDeclarators(VariableDeclarators ast) {

    }

    @Override
    public void enterVariableDeclarator(VariableDeclarator ast) {

    }

    @Override
    public void exitVariableDeclarator(VariableDeclarator ast) {

    }

    @Override
    public void enterVariableInitializer(VariableInitializer ast) {

    }

    @Override
    public void exitVariableInitializer(VariableInitializer ast) {

    }

    @Override
    public void enterTerminalNode(TerminalNode ast) {
    }

    @Override
    public void exitTerminalNode(TerminalNode ast) {
    }
}
