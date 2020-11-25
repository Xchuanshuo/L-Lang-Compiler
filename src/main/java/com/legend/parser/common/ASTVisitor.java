package com.legend.parser.common;

import com.legend.parser.Program;
import com.legend.parser.ast.*;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 抽象语法树访问者
 */
public interface ASTVisitor<T> {

    T visitProgram(Program ast);

    T visitArrayInitializer(ArrayInitializer ast);

    T visitArrayCall(ArrayCall ast);

    T visitBlock(Block ast);

    T visitBlockStatement(BlockStatement ast);

    T visitBlockStatements(BlockStatements ast);

    T visitClassDeclaration(ClassDeclaration ast);

    T visitClassBody(ClassDeclaration.ClassBody ast);

    T visitMemberDeclaration(ClassDeclaration.MemberDeclaration ast);

    T visitClassOrInterfaceType(ClassOrInterfaceType ast);

    T visitExpr(Expr ast);

    T visitExprList(ExprList ast);

    T visitForControl(ForControl ast);

    T visitForInit(ForControl.ForInit forInit);

    T visitFunctionCall(FunctionCall ast);

    T visitFunctionDeclaration(FunctionDeclaration ast);

    T visitFormalParameterList(FunctionDeclaration.FormalParameterList ast);

    T visitFormalParameter(FunctionDeclaration.FormalParameter ast);

    T visitFunctionType(FunctionType ast);

    T visitLiteral(Literal ast);

    T visitParExpression(ParExpression ast);

    T visitStatement(Statement ast);

    T visitTerminalNode(TerminalNode ast);

    T visitTypeList(TypeList ast);

    T visitTypeType(TypeType ast);

    T visitTypeTypeOrVoid(TypeTypeOrVoid ast);

    T visitVariableDeclarator(VariableDeclarator ast);

    T visitVariableDeclarators(VariableDeclarators ast);

    T visitVariableInitializer(VariableInitializer ast);
}
