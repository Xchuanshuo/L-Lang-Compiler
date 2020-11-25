package com.legend.parser.common;

import com.legend.parser.Program;
import com.legend.parser.ast.*;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description
 */
public class DefaultASTListener extends BaseASTListener {

    @Override
    public void enterProgram(Program program) {
        System.out.println("进入程序");
    }

    @Override
    public void exitProgram(Program program) {
        System.out.println("退出程序");
    }

    @Override
    public void enterClassDeclaration(ClassDeclaration classDeclaration) {
        System.out.println("进入类声明: " + classDeclaration);
    }

    @Override
    public void exitClassDeclaration(ClassDeclaration classDeclaration) {
        System.out.println("退出类声明: " + classDeclaration);

    }

    @Override
    public void enterFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        System.out.println("进入函数声明: " + functionDeclaration);
    }

    @Override
    public void exitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        System.out.println("退出函数声明: " + functionDeclaration);
    }

    @Override
    public void enterArrayCall(ArrayCall arrayCall) {
        System.out.println("进入数组调用: " + arrayCall);

    }

    @Override
    public void exitArrayCall(ArrayCall arrayCall) {
        System.out.println("退出数组调用: " + arrayCall);

    }

    @Override
    public void enterArrayInitializer(ArrayInitializer arrayInitializer) {
        System.out.println("进入数组初始化: " + arrayInitializer);

    }

    @Override
    public void exitArrayInitializer(ArrayInitializer arrayInitializer) {
        System.out.println("退出数组初始化: " + arrayInitializer);
    }

    @Override
    public void enterBlock(Block block) {
        System.out.println("进入块语句: " + block);
    }

    @Override
    public void exitBlock(Block block) {
        System.out.println("退出块语句: " + block);
    }

    @Override
    public void enterBlockStatement(BlockStatement blockStatement) {
        super.enterBlockStatement(blockStatement);
    }

    @Override
    public void exitBlockStatement(BlockStatement blockStatement) {
        super.exitBlockStatement(blockStatement);
    }

    @Override
    public void enterClassOrInterfaceType(ClassOrInterfaceType classOrInterfaceType) {
        super.enterClassOrInterfaceType(classOrInterfaceType);
    }

    @Override
    public void exitClassOrInterfaceType(ClassOrInterfaceType classOrInterfaceType) {
        super.exitClassOrInterfaceType(classOrInterfaceType);
    }

    @Override
    public void enterExpr(Expr expr) {
        super.enterExpr(expr);
    }

    @Override
    public void exitExpr(Expr expr) {
        super.exitExpr(expr);
    }

    @Override
    public void enterExprList(ExprList exprList) {
        super.enterExprList(exprList);
    }

    @Override
    public void exitExprList(ExprList exprList) {
        super.exitExprList(exprList);
    }

    @Override
    public void enterForControl(ForControl forControl) {
        super.enterForControl(forControl);
    }

    @Override
    public void exitForControl(ForControl forControl) {
        super.exitForControl(forControl);
    }

    @Override
    public void enterFunctionCall(FunctionCall functionCall) {
        super.enterFunctionCall(functionCall);
    }

    @Override
    public void exitFunctionCall(FunctionCall functionCall) {
        super.exitFunctionCall(functionCall);
    }

    @Override
    public void enterFunctionType(FunctionType functionType) {
        super.enterFunctionType(functionType);
    }

    @Override
    public void exitFunctionType(FunctionType functionType) {
        super.exitFunctionType(functionType);
    }

    @Override
    public void enterLiteral(Literal literal) {
        super.enterLiteral(literal);
    }

    @Override
    public void exitLiteral(Literal literal) {
        super.exitLiteral(literal);
    }

    @Override
    public void enterParExpression(ParExpression parExpression) {
        super.enterParExpression(parExpression);
    }

    @Override
    public void exitParExpression(ParExpression parExpression) {
        super.exitParExpression(parExpression);
    }

    @Override
    public void enterPrimitiveType(PrimitiveType primitiveType) {
        super.enterPrimitiveType(primitiveType);
    }

    @Override
    public void exitPrimitiveType(PrimitiveType primitiveType) {
        super.exitPrimitiveType(primitiveType);
    }

    @Override
    public void enterStatement(Statement statement) {
        super.enterStatement(statement);
    }

    @Override
    public void exitStatement(Statement statement) {
        super.exitStatement(statement);
    }

    @Override
    public void enterTypeList(TypeList typeList) {
        super.enterTypeList(typeList);
    }

    @Override
    public void exitTypeList(TypeList typeList) {
        super.exitTypeList(typeList);
    }

    @Override
    public void enterTypeType(TypeType typeType) {
        super.enterTypeType(typeType);
    }

    @Override
    public void exitTypeType(TypeType typeType) {
        super.exitTypeType(typeType);
    }

    @Override
    public void enterTypeTypeOrVoid(TypeTypeOrVoid typeTypeOrVoid) {
        super.enterTypeTypeOrVoid(typeTypeOrVoid);
    }

    @Override
    public void exitTypeTypeOrVoid(TypeTypeOrVoid typeTypeOrVoid) {
        super.exitTypeTypeOrVoid(typeTypeOrVoid);
    }

    @Override
    public void enterVariableDeclarators(VariableDeclarators variableDeclarators) {
        super.enterVariableDeclarators(variableDeclarators);
    }

    @Override
    public void exitVariableDeclarators(VariableDeclarators variableDeclarators) {
        super.exitVariableDeclarators(variableDeclarators);
    }

    @Override
    public void enterVariableDeclarator(VariableDeclarator variableDeclarator) {
        super.enterVariableDeclarator(variableDeclarator);
    }

    @Override
    public void exitVariableDeclarator(VariableDeclarator variableDeclarator) {
        super.exitVariableDeclarator(variableDeclarator);
    }

    @Override
    public void enterVariableInitializer(VariableInitializer variableInitializer) {
        super.enterVariableInitializer(variableInitializer);
    }

    @Override
    public void exitVariableInitializer(VariableInitializer variableInitializer) {
        super.exitVariableInitializer(variableInitializer);
    }

    @Override
    public void enterClassBody(ClassDeclaration.ClassBody classBody) {

    }

    @Override
    public void exitClassBody(ClassDeclaration.ClassBody classBody) {

    }

    @Override
    public void enterMemberDeclaration(ClassDeclaration.MemberDeclaration memberDeclaration) {

    }

    @Override
    public void exitMemberDeclaration(ClassDeclaration.MemberDeclaration memberDeclaration) {

    }

    @Override
    public void enterBlockStatements(BlockStatements blockStatements) {

    }

    @Override
    public void exitBlockStatements(BlockStatements blockStatements) {

    }

    @Override
    public void enterTerminalNode(TerminalNode terminalNode) {
        System.out.println("进入终端节点: " + terminalNode.getText());
    }

    @Override
    public void exitTerminalNode(TerminalNode terminalNode) {
        System.out.println("退出终端节点: " + terminalNode.getText());
    }
}
