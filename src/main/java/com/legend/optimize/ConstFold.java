package com.legend.optimize;

import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.ast.ASTNodeType;
import com.legend.parser.ast.Expr;
import com.legend.parser.ast.Literal;
import com.legend.parser.ast.TerminalNode;

/**
 * @author Legend
 * @data by on 21-2-17.
 * @description 常量折叠
 */
public class ConstFold {

    public static Expr constFold(Expr expr) {
        if (expr == null || expr instanceof Literal || expr instanceof TerminalNode) {
            return expr;
        }
        if (expr.isBinExpr()) {
            Expr left = expr.leftChild(), right = expr.rightChild();
            boolean isFolding = isFolding(left) && isFolding(right);
            if (isFolding) {
                return fold(expr);
            }
            if (left.isBinExpr()) {
                left = constFold(left);
            }
            if (right.isBinExpr()) {
                right = constFold(right);
            }
            Expr newExpr = new Expr(ASTNodeType.BINARY_EXP, expr.getToken());
            newExpr.addChild(left);
            newExpr.addChild(right);
            if (!isFolding(left) || !isFolding(right)) {
                return newExpr;
            }
            return constFold(newExpr);
        }
        return expr;
    }

    private static Expr fold(Expr expr) {
        Expr left = expr.leftChild(), right = expr.rightChild();
        String leftText = left.getToken().getText();
        String rightText = right.getToken().getText();
        switch (expr.getToken().getTokenType()) {
            case ADD:
                if (left instanceof Literal.StringLiteral
                        || right instanceof Literal.StringLiteral) {
                    if (left instanceof Literal.StringLiteral) {
                        leftText = leftText.substring(1, leftText.length() - 1);
                    }
                    if (right instanceof Literal.StringLiteral) {
                        rightText = rightText.substring(1, rightText.length() - 1);
                    }
                    Token token = new Token(TokenType.STRING_LITERAL, '"' + leftText + rightText + '"');
                    return new Literal.StringLiteral(token);
                } else if (left instanceof Literal.FloatLiteral || right instanceof Literal.FloatLiteral) {
                    float val = Float.parseFloat(leftText) + Float.parseFloat(rightText);
                    Token token = new Token(TokenType.FLOAT_LITERAL, String.valueOf(val));
                    return new Literal.FloatLiteral(token);
                } else if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) + Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case SUB:
                if (left instanceof Literal.FloatLiteral || right instanceof Literal.FloatLiteral) {
                    float val = Float.parseFloat(leftText) - Float.parseFloat(rightText);
                    Token token = new Token(TokenType.FLOAT_LITERAL, String.valueOf(val));
                    return new Literal.FloatLiteral(token);
                } else if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) - Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case MUL:
                if (left instanceof Literal.FloatLiteral || right instanceof Literal.FloatLiteral) {
                    float val = Float.parseFloat(leftText) * Float.parseFloat(rightText);
                    Token token = new Token(TokenType.FLOAT_LITERAL, String.valueOf(val));
                    return new Literal.FloatLiteral(token);
                } else if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) * Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case DIV:
                if (left instanceof Literal.FloatLiteral || right instanceof Literal.FloatLiteral) {
                    float val = Float.parseFloat(leftText) / Float.parseFloat(rightText);
                    Token token = new Token(TokenType.FLOAT_LITERAL, String.valueOf(val));
                    return new Literal.FloatLiteral(token);
                } else if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) / Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case MOD:
                if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) % Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case BIT_AND:
                if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) & Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case BIT_OR:
                if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) | Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case XOR:
                if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) ^ Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case LSHIFT:
                if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) << Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
            case RSHIFT:
                if (left instanceof Literal.IntegerLiteral || right instanceof Literal.IntegerLiteral) {
                    int val = Integer.parseInt(leftText) >> Integer.parseInt(rightText);
                    Token token = new Token(TokenType.DECIMAL_LITERAL, String.valueOf(val));
                    return new Literal.IntegerLiteral(token);
                }
                break;
        }
        return expr;
    }

    // 是字面量 并且不是boolean和null字面量的情况下可折叠
    private static boolean isFolding(Expr expr) {
        if (!(expr instanceof Literal)) {
            return false;
        }
        return !(expr instanceof Literal.BooleanLiteral
                || expr instanceof Literal.NullLiteral);
    }
}
