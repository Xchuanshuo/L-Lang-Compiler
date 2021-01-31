package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;
import com.legend.parser.common.PriorityTable;

import java.util.ArrayList;
import java.util.List;

import static com.legend.lexer.Keyword.Key.IF;
import static com.legend.lexer.Keyword.Key.SUPER;
import static com.legend.lexer.Keyword.Key.THIS;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 表达式
 *      left:  E(k) = E(k) op(k) E(k+1) | E(k+1)
 *      right: E(k) = E(k+1) E_(k)
 *             expr.left = E(k+1) expr.op = op(k) expr.right = E_(k)
 *             E_(k) = op(k) E(k+1) E_(k) | ε
 *
 *             e(k) = e(k+1) (op(k) e(k+1))*
 *
 *             E(t) = f E_(t) | u E_(t)
 *             E_(t) = op(t) E(t) E_(t) | ε
 *             E(t) = (f | u) (op(t) E(t))*
 *             id | functionCall | literal | '(' expression ')' |
 *             ! expression | arrayCall
 */
public class Expr extends ASTNode {

    private static final PriorityTable table = new PriorityTable();
    private String operator;

    public Expr() {
        super();
    }

    public Expr(ASTNodeType type) {
        this(type, null);
    }

    public Expr(ASTNodeType type, Token token) {
        this.astNodeType = type;
        this.token = token;
        if (token != null) {
            this.operator = this.remark = token.getText();
        }
    }

    private static ASTNode e(PeekTokenIterator it, int k) throws ParseException {
        if (k >= table.size()) {
            return top(it);
        }
        ASTNode node1 = e(it, k + 1);
        if (node1 != null) {
            while (it.hasNext()) {
                Token token = it.peek();
                if (!table.getOperator(k).contains(token.getText())) break;
                it.next();
                ASTNode node2 = e(it, k + 1);
                if (node2 == null) {
                    throw new ParseException(token);
                }
                Expr expr1 = new Expr(ASTNodeType.BINARY_EXP, token);
                expr1.addChild(node1);
                expr1.addChild(node2);
                node1 = expr1;
            }
        }
        // 解决 = | += | =+.. 赋值运算符的右结合性问题 node1为当前节点
        // 1.将[当前节点]左子树的右孩子变成[当前节点]的左孩子
        // 2.将[当前节点]左子树链接到[当前节点的父亲节点]
        // 3.将[当前节点]变成左子树的右孩子
        while (node1 != null && node1.astNodeType == ASTNodeType.BINARY_EXP
                && node1.getToken().isAssignOperator()) {
            ASTNode leftChild = ((Expr) node1).leftChild();
            boolean condition = leftChild != null &&
                    leftChild.astNodeType == ASTNodeType.BINARY_EXP
                    && leftChild.getToken().isAssignOperator();
            if (!condition) break;
            ((Expr) node1).setLeftChild(leftChild.getChild(1));
            leftChild.setParent(node1.getParent());
            ((Expr) leftChild).setRightChild(node1);
            node1 = leftChild;
        }
        return node1;
    }

    private static ASTNode top(PeekTokenIterator it) throws ParseException {
        if (!it.hasNext()) {
            return null;
        }
        Token token = it.peek();
        String text = token.getText();
        if (token.isId() || token.isKeyword()) {
            if (token.isBaseType() || token.isThisOrSuper()){
                it.nextMatch(TokenType.KEYWORD);
                if (token.isThisOrSuper() && !it.topIsEqual("(")
                        && !it.topIsEqual(".")) {
                    if (!Keyword.isMatchKey(THIS, token.getText())) {
                        throw new ParseException(token);
                    }
                }
            } else {
                it.nextMatch(TokenType.IDENTIFIER);
            }
            TerminalNode identifier = new TerminalNode(token);
            if (it.hasNext()) {
                if (it.peek().getText().equals("(")) { // 处理函数调用
                    return FunctionCall.parse(it, identifier);
                } else if (it.peek().getText().equals("[")) { // 处理数组调用
                    return ArrayCall.parse(it, identifier);
                }
            }
            return identifier;
        } else if (token.isLiteral()) {
            return Literal.parse(it);
        } else if (text.equals("(")) {
            it.nextMatch("(");
            Expr expr = (Expr) e(it, 0);
            it.nextMatch(")");
            return expr;
        } else if (text.equals("!")) {
            Expr expr = new Expr(ASTNodeType.UNARY_EXP, it.nextMatch(TokenType.BANG));
            expr.addChild(e(it, 0));
            return expr;
        }
        return null;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        return e(it, 0);
    }

    public boolean isDotExpr() {
        if (token == null) return false;
        return token.getText().equals(".");
    }

    public boolean isAssignExpr() {
        if (token == null) return false;
        return token.getText().equals("=");
    }

    public Expr expr(int idx) {
        return getASTNode(Expr.class, idx);
    }

    public Expr leftChild() {
        return expr(0);
    }

    public void setLeftChild(ASTNode astNode) {
        getChildren().set(0, astNode);
        astNode.setParent(this);
    }

    public Expr rightChild() {
        return expr(1);
    }

    public void setRightChild(ASTNode astNode) {
        getChildren().set(1, astNode);
        astNode.setParent(this);
    }


    public List<Expr> exprs() {
        return getASTNodes(Expr.class);
    }

    public FunctionCall functionCall() {
        return getASTNode(FunctionCall.class);
    }

    public ArrayCall arrayCall() {
        return getASTNode(ArrayCall.class);
    }

    public TerminalNode IDENTIFIER() {
        return getTerminalNode(TokenType.IDENTIFIER);
    }

    public Literal literal() {
        return getASTNode(Literal.class);
    }

    public TerminalNode THIS() {
        return getTerminalNode(THIS);
    }

    public TerminalNode SUPER() {
        return getTerminalNode(SUPER);
    }

    public String getOperator() {
        return operator;
    }

}
