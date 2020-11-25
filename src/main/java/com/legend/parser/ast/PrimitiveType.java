package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.parser.common.PeekTokenIterator;

import static com.legend.lexer.Keyword.Key.*;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 原始类型
 */
public class PrimitiveType extends ASTNode {

    public PrimitiveType() {
        this.astNodeType = ASTNodeType.PRIMITIVE_TYPE;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        PrimitiveType primitiveType = new PrimitiveType();
        primitiveType.addChild(new TerminalNode(it.next()));
        return primitiveType;
    }

    public TerminalNode CHAR() {
        return getTerminalNode(Keyword.getValueByKey(CHAR));
    }

    public TerminalNode STRING() {
        return getTerminalNode(Keyword.getValueByKey(STRING));
    }

    public TerminalNode FLOAT() {
        return getTerminalNode(Keyword.getValueByKey(FLOAT));
    }

    public TerminalNode INT() {
        return getTerminalNode(Keyword.getValueByKey(INT));
    }

    public TerminalNode BOOLEAN() {
        return getTerminalNode(Keyword.getValueByKey(BOOLEAN));
    }

}
