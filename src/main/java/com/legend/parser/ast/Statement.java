package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.TokenType;
import com.legend.parser.Parser;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

import static com.legend.lexer.Keyword.Key.*;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 语句
 */
public class Statement extends ASTNode {

    public Statement() {
        this.astNodeType = ASTNodeType.STATEMENT;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        if (!it.hasNext()) return null;
        Statement statement = new Statement();
        String text = it.peek().getText();
        if (text.equals("{")) {
            ASTNode block = Block.parse(it);
            statement.addChild(block);
        } else if (Keyword.isMatchKey(IF, text)) {
            parseIfStmt(statement, it);
        } else if (Keyword.isMatchKey(FOR, text)) {
            parseForStmt(statement, it);
        } else if (Keyword.isMatchKey(WHILE, text)) {
            parseWhileStmt(statement, it);
        } else if (Keyword.isMatchKey(SWITCH, text)) {
            // todo
        } else if (Keyword.isMatchKey(RETURN, text)) {
            parseReturnStmt(statement, it);
        } else if (Keyword.isMatchKey(BREAK, text)) {
            TerminalNode breakNode = new TerminalNode(it.nextMatch(BREAK));
            statement.addChild(breakNode);
            if (it.topIsEqual(TokenType.SEMICOLON)) {
                it.nextMatch(TokenType.SEMICOLON);
            }
        } else if (Keyword.isMatchKey(CONTINUE, text)) {
            TerminalNode returnNode = new TerminalNode(it.nextMatch(CONTINUE));
            statement.addChild(returnNode);
            if (it.topIsEqual(TokenType.SEMICOLON)) {
                it.nextMatch(TokenType.SEMICOLON);
            }
        } else if (it.peek().getTokenType() == TokenType.SEMICOLON) {
            TerminalNode terminalNode = new TerminalNode(it.nextMatch(TokenType.SEMICOLON));
            statement.addChild(terminalNode);
        } else {
            ASTNode expr = Expr.parse(it);
            if (expr != null) {
                statement.addChild(expr);
            }
        }
        if (statement.getChildren().size() == 0) {
            return null;
        }
        return statement;
    }

    // 解析if语句块
    private static void parseIfStmt(Statement statement, PeekTokenIterator it) throws ParseException {
        TerminalNode ifNode = new TerminalNode(it.nextMatch(IF));
        statement.addChild(ifNode);
        ASTNode parExpression = ParExpression.parse(it);
        statement.addChild(parExpression);
        ASTNode ifStmtBlock = Statement.parse(it);
        statement.addChild(ifStmtBlock);
        if (it.topIsEqual(TokenType.SEMICOLON)) {
            it.nextMatch(TokenType.SEMICOLON);
        }
        if (it.hasNext() && Keyword.isMatchKey(ELSE, it.peek().getText())) {
            TerminalNode elseNode = new TerminalNode(it.nextMatch(ELSE));
            statement.addChild(elseNode);

            ASTNode elseStmtBlock = Statement.parse(it);
            statement.addChild(elseStmtBlock);
        }
    }

    // 解析for循环语句
    private static void parseForStmt(Statement statement, PeekTokenIterator it) throws ParseException {
        TerminalNode terminalNode = new TerminalNode(it.nextMatch(FOR));
        statement.addChild(terminalNode);
        it.nextMatch("(");

        ASTNode forControl = ForControl.parse(it);
        statement.addChild(forControl);
        it.nextMatch(")");
        ASTNode s = Statement.parse(it);
        statement.addChild(s);
    }

    // 解析while循环语句
    private static void parseWhileStmt(Statement statement, PeekTokenIterator it) throws ParseException {
        TerminalNode terminalNode = new TerminalNode(it.nextMatch(WHILE));
        statement.addChild(terminalNode);
        ASTNode parExpression = ParExpression.parse(it);
        statement.addChild(parExpression);
        ASTNode s = Statement.parse(it);
        statement.addChild(s);
    }

    // 解析return
    private static void parseReturnStmt(Statement statement, PeekTokenIterator it) throws ParseException {
        TerminalNode returnNode = new TerminalNode(it.nextMatch(RETURN));
        statement.addChild(returnNode);
        ASTNode expr = Expr.parse(it);
        if (expr != null) {
            statement.addChild(expr);
        }
        if (it.topIsEqual(TokenType.SEMICOLON)) {
            it.nextMatch(TokenType.SEMICOLON);
        }
    }

    public TerminalNode IF() {
        return getTerminalNode(IF);
    }

    public TerminalNode ELSE() {
        return getTerminalNode(ELSE);
    }

    public TerminalNode FOR() {
        return getTerminalNode(FOR);
    }

    public TerminalNode WHILE() {
        return getTerminalNode(WHILE);
    }

    public TerminalNode SWITCH() {
        return getTerminalNode(SWITCH);
    }

    public TerminalNode RETURN() {
        return getTerminalNode(RETURN);
    }

    public TerminalNode BREAK() {
        return getTerminalNode(BREAK);
    }

    public TerminalNode CONTINUE() {
        return getTerminalNode(CONTINUE);
    }

    public Block block() {
        return getASTNode(Block.class);
    }

    public ForControl forControl() {
        return getASTNode(ForControl.class);
    }

    public boolean isSemicolon() {
        TerminalNode t = getTerminalNode(TokenType.SEMICOLON);
        if (t != null) return true;
        return false;
    }

    public ParExpression parExpr() {
        return getASTNode(ParExpression.class);
    }

    public Expr expr() {
        if (RETURN() != null) {
            return getASTNode(Expr.class, 1);
        }
        return getASTNode(Expr.class, 0);
    }

    public Statement statement(int idx) {
        return getASTNode(Statement.class, idx);
    }
}
