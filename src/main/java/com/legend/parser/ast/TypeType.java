package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

import static com.legend.lexer.Keyword.Key.*;


/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 类型 包含原始类型,类或接口类型,函数类型
 */
public class TypeType extends ASTNode {

    public TypeType() {
        this.astNodeType = ASTNodeType.TYPE_TYPE;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        TypeType typeType = new TypeType();
        Token token = it.peek();
        if (token.getTokenType() == TokenType.KEYWORD) {
            if (isPrimitiveType(token)) {
                typeType.addChild(PrimitiveType.parse(it));
            } else if (token.getText().equals(Keyword.getValueByKey(FUNCTION))) {
                typeType.addChild(FunctionType.parse(it));
            }
        } else if (token.isId()) {
            typeType.addChild(ClassOrInterfaceType.parse(it));
        }
        if (typeType.getChildren().size() == 0) {
            return null;
        }
        // 匹配类型后继续匹配([])*部分
        while (it.hasNext() && it.peek().getText().equals("[")) {
            typeType.addChild(new TerminalNode(it.nextMatch("[")));
            if (!it.topIsEqual("]")) {
                return null;
            }
            typeType.addChild(new TerminalNode(it.nextMatch("]")));
        }
        return typeType;
    }

    public ClassOrInterfaceType classOrInterfaceType() {
        return getASTNode(ClassOrInterfaceType.class);
    }

    public FunctionType functionType() {
        return getASTNode(FunctionType.class);
    }

    public PrimitiveType primitiveType() {
        return getASTNode(PrimitiveType.class);
    }

    public List<TerminalNode> leftParen() {
        return getTerminalNodes("[");
    }

    public List<TerminalNode> rightParen() {
        return getTerminalNodes("]");
    }

    private static boolean isPrimitiveType(Token token) {
        if (token == null || token.getTokenType() != TokenType.KEYWORD) {
            return false;
        }
        String text = token.getText();
        return text.equals(Keyword.getValueByKey(CHAR)) ||
                text.equals(Keyword.getValueByKey(STRING)) ||
                text.equals(Keyword.getValueByKey(INT)) ||
                text.equals(Keyword.getValueByKey(FLOAT)) ||
                text.equals(Keyword.getValueByKey(BOOLEAN));
    }
}
