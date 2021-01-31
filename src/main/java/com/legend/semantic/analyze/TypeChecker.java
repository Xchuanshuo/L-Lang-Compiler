package com.legend.semantic.analyze;

import com.legend.common.BuiltInFunction;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.ast.*;
import com.legend.parser.common.BaseASTListener;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.semantic.PrimitiveType;

/**
 * @author Legend
 * @data by on 20-11-17.
 * @description ast第4次扫描: 类型检查 隐式类型转换
 *  1.赋值表达式 2.变量初始化 3.表达式操作数
 *  4.返回值类型
 *
 *  赋值:
 *  1.整形可以装换为浮点型
 *  2.子类的对象可以赋值给父类
 *  3.函数赋值, 要求签名是一致的(返回值,参数)
 *  4.数组传的下标只能是整形
 */
public class TypeChecker extends BaseASTListener {

    private AnnotatedTree at;

    public TypeChecker(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public void exitVariableDeclarator(VariableDeclarator ast) {
        if (ast.variableInitializer() != null) {
            Variable variable = (Variable) at.symbolOfNode.get(ast.identifier());
            Type type1 = variable.getType();
            Type type2 = at.typeOfNode.get(ast.variableInitializer());
            checkAssign(type1, type2, ast.identifier(), ast.variableInitializer(), ast.identifier());
        }
    }

    @Override
    public void exitExpr(Expr ast) {
        if (ast.getToken() == null && ast.getChildren().size() < 2) {
            return;
        }
        Type type1 = at.typeOfNode.get(ast.getChild(0));
        Type type2 = at.typeOfNode.get(ast.getChild(1));
        switch (ast.getToken().getTokenType()) {
            case ADD:
                if (type1 != PrimitiveType.String && type2 != PrimitiveType.String) {
                    checkNumericOperand(type1, (Expr) ast.getChild(0), ast);
                    checkNumericOperand(type2, (Expr) ast.getChild(1), ast);
                }
                typeImplicitCast(ast);
                break;
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                checkNumericOperand(type1, (Expr) ast.getChild(0), ast);
                checkNumericOperand(type2, (Expr) ast.getChild(1), ast);
                typeImplicitCast(ast);
                break;
            case LOGIC_AND:
            case LOGIC_OR:
                checkBooleanOperand(type1, (Expr) ast.getChild(0), ast);
                checkBooleanOperand(type2, (Expr) ast.getChild(1), ast);
                break;
            case ASSIGN:
                checkAssign(type1, type2, ast.getChild(0), ast.getChild(1), ast);
                break;
            case ADD_ASSIGN:
                checkAssign(type1, type2, ast.getChild(0), ast.getChild(1), ast);
                typeImplicitCast(ast);
                break;
            case SUB_ASSIGN:
            case MUL_ASSIGN:
            case DIV_ASSIGN:
            case MOD_ASSIGN:
                checkNumericOperand(type2, (Expr) ast.getChild(1), ast);
                checkAssign(type1, type2, ast.getChild(0), ast.getChild(1), ast);
                typeImplicitCast(ast);
                break;
            case LSHIFT:
            case RSHIFT:
            case BIT_AND:
            case BIT_OR:
            case XOR:
            case AND_ASSIGN:
            case OR_ASSIGN:
            case XOR_ASSIGN:
            case LSHIFT_ASSIGN:
            case RSHIFT_ASSIGN:
            case URSHIFT_ASSIGN:
                checkIntOperand(type1, (Expr) ast.getChild(0), ast);
                checkIntOperand(type2, (Expr) ast.getChild(1), ast);
                break;
            case LT:
            case LE:
            case GT:
            case GE:
                checkNumericOperand(type1, (Expr) ast.getChild(0), ast);
                checkNumericOperand(type2, (Expr) ast.getChild(1), ast);
                typeImplicitCast(ast);
                break;
        }
    }

    @Override
    public void exitArrayCall(ArrayCall ast) {
        if (ast.exprList() != null) {
            for (Expr expr : ast.exprList()) {
                Type type = at.typeOfNode.get(expr);
                checkIntOperand(type, expr, ast);
            }
        }
    }

    // 隐式类型转换
    private void typeImplicitCast(Expr expr) {
        Type type1 = at.typeOfNode.get(expr.leftChild());
        Type type2 = at.typeOfNode.get(expr.rightChild());
        Type upperType = PrimitiveType.getUpperType(type1, type2);
        BuiltInFunction.BuiltIn key = BuiltInFunction.getKeyByType(upperType);
        if (key == null) return;
        if (type1 != upperType) {
            FunctionCall castFunCall = new FunctionCall();
            Token token = new Token(TokenType.KEYWORD, BuiltInFunction.getValueByKey(key));
            ExprList exprList = new ExprList();
            exprList.addChild(expr.leftChild());
            castFunCall.addChild(new TerminalNode(token));
            castFunCall.addChild(exprList);
            expr.setLeftChild(castFunCall);
        }
        if (type2 != upperType) {
            FunctionCall castFunCall = new FunctionCall();
            Token token = new Token(TokenType.KEYWORD, BuiltInFunction.getValueByKey(key));
            ExprList exprList = new ExprList();
            exprList.addChild(expr.rightChild());
            castFunCall.addChild(new TerminalNode(token));
            castFunCall.addChild(exprList);
            expr.setRightChild(castFunCall);
        }
    }

    private void checkNumericOperand(Type type, Expr operand, Expr ast) {
        if (!PrimitiveType.isNumeric(type)) {
            at.log("operand for arithmetic operation must be numeric: "
                    + operand.getText(), ast);
        }
    }

    private void checkBooleanOperand(Type type, Expr operand, Expr ast) {
        if (type != PrimitiveType.Boolean) {
            at.log("operand for logical operation must boolean:"
                    + operand.getText(), ast);
        }
    }

    private void checkIntOperand(Type type, Expr operand, Expr ast) {
        if (type != PrimitiveType.Integer) {
            at.log("operand for current operation except integer: ["
                    + operand.getText() + "] but " + type, ast);
        }
    }

    /**
     * 赋值操作类型检查
     * @param type1 被赋值类型
     * @param type2 赋值类型
     * @param operand1 操作数1
     * @param operand2 操作数2
     * @param ast 对应的表达式ast节点
     */
    private void checkAssign(Type type1, Type type2,
                             ASTNode operand1, ASTNode operand2,
                             ASTNode ast) {
        if (PrimitiveType.isNumeric(type2)) {
            if (!checkNumericAssign(type2, type1)) {
                at.log("can not assign " + operand2.getText() + " of type "
                        + type2 + " to " + operand1.getText() + " of type " + type1, ast);
            }
        } else if (type2 instanceof Class) {
            // todo 检查类的兼容性
        } else if (type2 instanceof Function) {
            // todo 检查函数的兼容性
        }
    }

    /**
     * 检查数值类型的赋值
     * @param from 来源类型
     * @param to 目标类型
     * @return 来源类型是否能赋值目标类型
     */
    private boolean checkNumericAssign(Type from, Type to) {
        boolean canAssign = false;
        if (to == PrimitiveType.Double) {
            canAssign = PrimitiveType.isNumeric(from);
        } else if (to == PrimitiveType.Float) {
            canAssign = (from == PrimitiveType.Byte
                    || from == PrimitiveType.Short
                    || from == PrimitiveType.Integer
                    || from == PrimitiveType.Long
                    || from == PrimitiveType.Float);
        } else if (to == PrimitiveType.Long) {
            canAssign = (from == PrimitiveType.Byte
                    || from == PrimitiveType.Short
                    || from == PrimitiveType.Integer
                    || from == PrimitiveType.Long);
        } else if (to == PrimitiveType.Integer) {
            canAssign = (from == PrimitiveType.Byte
                    || from == PrimitiveType.Short
                    || from == PrimitiveType.Integer);
        } else if (to == PrimitiveType.Short) {
            canAssign = (from == PrimitiveType.Byte
                    || from == PrimitiveType.Short);
        } else if (to == PrimitiveType.Byte) {
            canAssign = from == PrimitiveType.Byte;
        } else if (to == PrimitiveType.String) {
            canAssign = from == PrimitiveType.String;
        } else if (to instanceof ArrayType) {
            canAssign = from == to;
        }
        return canAssign;
    }
}
