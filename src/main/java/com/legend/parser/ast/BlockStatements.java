package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-12.
 * @description 语句块集合
 */
public class BlockStatements extends ASTNode {

    public BlockStatements() {
        this.astNodeType = ASTNodeType.BLOCK_STATEMENTS;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        BlockStatements statements = new BlockStatements();
        BlockStatement blockStatement;
        while ((blockStatement = (BlockStatement) BlockStatement.parse(it)) != null) {
            if (blockStatement.statement() != null &&
                blockStatement.statement().isSemicolon()) {
                // 从ast过滤掉分号语句
                continue;
            }
            statements.addChild(blockStatement);
        }
        return statements;
    }

    public List<BlockStatement> blockStatements() {
        return getASTNodes(BlockStatement.class);
    }
}
