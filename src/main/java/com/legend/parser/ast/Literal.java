package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Token;
import com.legend.parser.common.PeekTokenIterator;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description 字面量
 */
public class Literal extends Expr {

    public Literal(ASTNodeType type, Token token) {
        this.astNodeType = type;
        this.token = token;
        this.remark = token.getText() + "";
    }

    public static Literal parse(PeekTokenIterator it) throws ParseException {
        Token token = it.next();
        return createLiteral(token);
    }

    public static Literal createLiteral(Token token) {
        if (token.isLiteral()) {
            switch (token.getTokenType()) {
                case OCT_LITERAL:
                    token.setText(Integer.valueOf(token.getText(), 8) + "");
                    return new IntegerLiteral(token);
                case HEX_LITERAL:
                    token.setText(Integer.valueOf(token.getText().substring(2), 16) + "");
                case DECIMAL_LITERAL:
                    return new IntegerLiteral(token);
                case FLOAT_LITERAL:
                    return new FloatLiteral(token);
                case BOOL_LITERAL:
                    return new BooleanLiteral(token);
                case CHAR_LITERAL:
                    return new CharLiteral(token);
                case STRING_LITERAL:
                    return new StringLiteral(token);
                case NULL_LITERAL:
                    return new NullLiteral(token);
            }
        }
        return null;
    }

    public String getText() {
        return token.getText();
    }

    public static class IntegerLiteral extends Literal {
        public IntegerLiteral(Token token) {
            super(ASTNodeType.INTEGER_LITERAL, token);
        }
    }

    public static class FloatLiteral extends Literal {
        public FloatLiteral(Token token) {
            super(ASTNodeType.FLOAT_LITERAL, token);
        }
    }

    public static class BooleanLiteral extends Literal {
        public BooleanLiteral( Token token) {
            super(ASTNodeType.BOOL_LITERAL, token);
        }
    }

    public static class StringLiteral extends Literal {
        public StringLiteral(Token token) {
            super(ASTNodeType.STRING_LITERAL, token);
        }
    }

    public static class CharLiteral extends Literal {
        public CharLiteral(Token token) {
            super(ASTNodeType.CHAR_LITERAL, token);
        }
    }

    public static class NullLiteral extends Literal {
        public NullLiteral(Token token) {
            super(ASTNodeType.NULL_LITERAL, token);
        }
    }
}
