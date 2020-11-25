package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.parser.common.PeekTokenIterator;

import static com.legend.lexer.Keyword.Key.VOID;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 普通类型或void类型
 */
public class TypeTypeOrVoid extends ASTNode {

    public TypeTypeOrVoid() {
        this.astNodeType = ASTNodeType.TYPE_TYPE_OR_VOID;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        TypeTypeOrVoid typeTypeOrVoid = new TypeTypeOrVoid();
        Token token = it.peek();
        if (token.getText().equals(Keyword.getValueByKey(VOID))) {
            it.next();
            typeTypeOrVoid.addChild(new TerminalNode(token));
        } else {
            ASTNode typeType = TypeType.parse(it);
            typeTypeOrVoid.addChild(typeType);
        }
        if (typeTypeOrVoid.getChildren().size() == 0) {
            return null;
        }
        return typeTypeOrVoid;
    }

    public TerminalNode VOID() {
        return getTerminalNode(Keyword.getValueByKey(VOID));
    }

    public TypeType typeType() {
        return getASTNode(TypeType.class);
    }
}
