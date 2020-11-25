package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.TokenType;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.PeekTokenIterator;

import static com.legend.lexer.Keyword.Key.CLASS;
import static com.legend.parser.common.PeekUtils.isFuncDeclaration;
import static com.legend.parser.common.PeekUtils.isVariableDeclaration;

/**
 * @author Legend
 * @data by on 20-11-12.
 * @description 块语句
 */
public class BlockStatement extends ASTNode {

    public BlockStatement() {
        this.astNodeType = ASTNodeType.BLOCK_STATEMENT;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        if (!it.hasNext()) return null;
        BlockStatement blockStatement = new BlockStatement();
        String text = it.peek().getText();
        if (text.equals(Keyword.getValueByKey(CLASS))) { // 类声明
            ASTNode classDeclaration = ClassDeclaration.parse(it);
            blockStatement.addChild(classDeclaration);
        } else if (isFuncDeclaration(it)) { // 函数声明
            ASTNode funcDeclaration = FunctionDeclaration.parse(it);
            blockStatement.addChild(funcDeclaration);
        } else if (isVariableDeclaration(it)){ // 变量声明
            ASTNode variableDeclaration = VariableDeclarators.parse(it);
            blockStatement.addChild(variableDeclaration);
            if (it.topIsEqual(TokenType.SEMICOLON)) {
                it.nextMatch(TokenType.SEMICOLON);
            }
        } else { // 语句
            ASTNode statement = Statement.parse(it);
            if (statement != null) {
                blockStatement.addChild(statement);
            }
        }
        if (blockStatement.getChildren().size() == 0) {
            return null;
        }
        return blockStatement;
    }

    public ClassDeclaration classDeclaration() {
        return getASTNode(ClassDeclaration.class);
    }

    public FunctionDeclaration functionDeclaration() {
        return getASTNode(FunctionDeclaration.class);
    }

    public VariableDeclarators variableDeclarators() {
        return getASTNode(VariableDeclarators.class);
    }

    public Statement statement() {
        return getASTNode(Statement.class);
    }
}
