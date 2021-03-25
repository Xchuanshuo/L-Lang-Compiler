package com.legend.semantic.analyze;

import com.legend.parser.ast.*;
import com.legend.parser.common.BaseASTListener;
import com.legend.semantic.AnnotatedTree;
import com.legend.semantic.Function;
import com.legend.semantic.Type;
import com.legend.semantic.VoidType;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author Legend
 * @data by on 20-11-18.
 * @description ast第5遍扫描: 语义校验器
 * 1.break
 *  只能出现在循环语句或case语句中
 *
 * 2.return 语句
 *   1) 函数声明了返回值,就一定要有return语句,除非返回值是void类型
 *   2)类的构造函数里如果用到return,不能带返回值
 *   3)return语句只能出现在函数里
 *   4)返回值类型检查(TypeChecker里做)
 *
 * break语句和return后的代码是死代码 应该消除
 *
 * 3.左值
 *   1)标注左值(不标注就是右值)
 *   2)检查表达式能否生成合法的左值
 *
 * 4.类的声明不能在函数里
 *
 * 5.super()和this(). 只能是构造函数的第一句 RefSolver中实现了
 */
public class SemanticValidator extends BaseASTListener {

    private AnnotatedTree at;

    public SemanticValidator(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public void exitExpr(Expr ast) {
    }

    @Override
    public void exitClassDeclaration(ClassDeclaration ast) {
        if (at.enclosingFunctionOfNode(ast) != null) {
            at.log("can not declare class inside function", ast);
        }
    }

    @Override
    public void exitFunctionDeclaration(FunctionDeclaration ast) {
        // 02-01 函数定义了返回值,就一定要有相应的return语句
        // todo 更完善的是要进行控制流计算,不是仅仅有一个return语句就行了
        if (ast.typeTypeOrVoid() != null) {
            if (ast.BUILTIN() == null && !hasReturnStatement(ast)) {
                Type returnType = at.typeOfNode.get(ast.typeTypeOrVoid());
                if (returnType != VoidType.instance()) {
                    at.log("return statement expected in Function", ast);
                }
            }
        }
    }

    @Override
    public void exitStatement(Statement ast) {
        if (ast.RETURN() != null) {
            Function function = at.enclosingFunctionOfNode(ast);
            if (function == null) {
                at.log("return statement not in function", ast);
            } else {
                if (ast.expr() != null) {
                    Type exprType = at.typeOfNode.get(ast.expr());
                    if (function.isConstructor()) {
                        at.log("can not return a value from constructor", ast);
                    } else if (!exprType.isType(function.returnType())) {
                        at.log("can not match a correct return value type", ast);
                    }
                } else if (function.returnType() != VoidType.instance()) {
                    at.log("can not match a correct return value type", ast);
                }
                if (isExistDeadCode(ast)) {
                    at.log("exist unreachable statement after return!", ast);
                }
            }
        } else if (ast.BREAK() != null) {
            if (!checkBreak(ast)) {
                at.log("break statement not in loop or switch statements", ast);
            } else if (isExistDeadCode(ast)) {
                at.log("exist unreachable statement after break!", ast);
            }
        }
    }

    // 检查函数里有没有return语句
    private boolean hasReturnStatement(ASTNode ast) {
        boolean rtn = false;
        for (int i = 0;i < ast.getChildren().size();i++) {
            ASTNode child = ast.getChildren().get(i);
            if (child instanceof Statement && ((Statement) child).RETURN() != null) {
                rtn = true;
            } else if (!(child instanceof FunctionDeclaration
                    || child instanceof ClassDeclaration)) {
                rtn = hasReturnStatement(child);
            }
            if (rtn) break;
        }
        return rtn;
    }

    // break语句只能出现在循环或switch-case语句里
    private boolean checkBreak(ASTNode ast) {
        ASTNode parent = ast.getParent();
        if (parent instanceof Statement &&
                (((Statement) parent).FOR() != null ||
                ((Statement) parent).WHILE() != null)) {
            return true;
        } else if (parent == null || parent instanceof FunctionDeclaration) {
            return false;
        }
        return checkBreak(parent);
    }

    private boolean isExistDeadCode(ASTNode ast) {
        ASTNode parent = null;
        if (ast.getParent() instanceof Statement) {
            // break or return statement -> statement
            parent = ast.getParent();
            if (parent.lastChild() != ast && ((Statement)parent).IF() == null) {
                return true;
            }
        } else {
            // break or return statement -> BlockStatement -> BlockStatements
            parent = ast.getParent().getParent();
            if (parent.lastChild().getChild(0) != ast) {
                return true;
            }
        }
        return false;
    }
}
