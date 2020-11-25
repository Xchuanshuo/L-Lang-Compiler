package com.legend.parser.common;

import com.legend.parser.Program;
import com.legend.parser.ast.*;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 抽象语法树监听器
 */
public interface ASTListener {

    void enterProgram(Program ast);

    void exitProgram(Program ast);

    void enterClassDeclaration(ClassDeclaration ast);

    void exitClassDeclaration(ClassDeclaration ast);

    void enterClassBody(ClassDeclaration.ClassBody ast);

    void exitClassBody(ClassDeclaration.ClassBody ast);

    void enterMemberDeclaration(ClassDeclaration.MemberDeclaration ast);

    void exitMemberDeclaration(ClassDeclaration.MemberDeclaration ast);

    void enterFunctionDeclaration(FunctionDeclaration ast);

    void exitFunctionDeclaration(FunctionDeclaration ast);

    void enterFormalParameterList(FunctionDeclaration.FormalParameterList ast);

    void exitFormalParameterList(FunctionDeclaration.FormalParameterList ast);

    void enterFormalParameter(FunctionDeclaration.FormalParameter ast);

    void exitFormalParameter(FunctionDeclaration.FormalParameter ast);

    void enterArrayCall(ArrayCall ast);

    void exitArrayCall(ArrayCall ast);

    void enterArrayInitializer(ArrayInitializer ast);

    void exitArrayInitializer(ArrayInitializer ast);

    void enterBlock(Block ast);

    void exitBlock(Block ast);

    void enterBlockStatement(BlockStatement ast);

    void exitBlockStatement(BlockStatement ast);

    void enterBlockStatements(BlockStatements ast);

    void exitBlockStatements(BlockStatements ast);

    void enterClassOrInterfaceType(ClassOrInterfaceType ast);

    void exitClassOrInterfaceType(ClassOrInterfaceType ast);

    void enterExpr(Expr ast);

    void exitExpr(Expr ast);

    void enterExprList(ExprList ast);

    void exitExprList(ExprList ast);

    void enterForControl(ForControl ast);

    void exitForControl(ForControl ast);

    void enterForInit(ForControl.ForInit ast);

    void exitForInit(ForControl.ForInit ast);

    void enterFunctionCall(FunctionCall ast);

    void exitFunctionCall(FunctionCall ast);

    void enterFunctionType(FunctionType ast);

    void exitFunctionType(FunctionType ast);

    void enterLiteral(Literal ast);

    void exitLiteral(Literal ast);

    void enterParExpression(ParExpression ast);

    void exitParExpression(ParExpression ast);

    void enterPrimitiveType(PrimitiveType ast);

    void exitPrimitiveType(PrimitiveType ast);

    void enterStatement(Statement ast);

    void exitStatement(Statement ast);

    void enterTypeList(TypeList ast);

    void exitTypeList(TypeList ast);

    void enterTypeType(TypeType ast);

    void exitTypeType(TypeType ast);

    void enterTypeTypeOrVoid(TypeTypeOrVoid ast);

    void exitTypeTypeOrVoid(TypeTypeOrVoid ast);

    void enterVariableDeclarators(VariableDeclarators ast);

    void exitVariableDeclarators(VariableDeclarators ast);

    void enterVariableDeclarator(VariableDeclarator ast);

    void exitVariableDeclarator(VariableDeclarator ast);

    void enterVariableInitializer(VariableInitializer ast);

    void exitVariableInitializer(VariableInitializer ast);

    void enterTerminalNode(TerminalNode ast);

    void exitTerminalNode(TerminalNode ast);
}
