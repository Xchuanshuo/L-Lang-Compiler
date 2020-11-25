package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.PeekTokenIterator;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 语句块
 */
public class Block extends ASTNode {

    public Block() {
        this.astNodeType = ASTNodeType.BLOCK;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        Block block = new Block();
        it.nextMatch("{");
        BlockStatements blockStatements = (BlockStatements) BlockStatements.parse(it);
//        if (blockStatements.blockStatements() != null) {
//            for (BlockStatement blockStatement : blockStatements.blockStatements()) {
//                block.addChild(blockStatement);
//            }
//        }
        block.addChild(blockStatements);
        it.nextMatch("}");
        return block;
    }

    public BlockStatements blockStatements() {
        return getASTNode(BlockStatements.class);
    }
}
