package com.legend.lexer;

import com.legend.common.AlphabetHelper;
import com.legend.common.CastArrayUtil;
import com.legend.exception.LexicalException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 词法分析器
 */
public class Lexer {

    public static List<Token> fromFile(String src) throws IOException, LexicalException {
        File file = new File(src);
        if (file.exists() && file.isFile()) {
            try (InputStream fis = new FileInputStream(src);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(new BufferedInputStream(fis)))){
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                Lexer lexer = new Lexer();
                return lexer.analyze(sb.toString());
            }
        } else {
            System.out.println("The file path is empty!-----{" + src + "}");
        }
        return new ArrayList<>();
    }

    public List<Token> analyze(String code) throws LexicalException {
        PeekCharIterator it = new PeekCharIterator(CastArrayUtil.toWrap(code), '\0');
        return analyze(it);
    }

    public List<Token> analyze(PeekCharIterator it) throws LexicalException {
        List<Token> tokens = new ArrayList<>();
        while (it.hasNext()) {
            char c = it.next();
            if (c == 0) break;
            char lookahead = it.peek();
            if (c == ' ' || c == '\n') continue;
            if (c == '/' && (lookahead == '/' ||  lookahead == '*')) {
                skipComments(it);
            } else if (AlphabetHelper.isLetter(c)) {
                it.putBack();
                tokens.add(makeIdOrKeyword(it));
            } else if (c == '\'' || c == '"') {
                it.putBack();
                tokens.add(makeString(it));
            } else if (AlphabetHelper.isDigit(c)) {
                it.putBack();
                tokens.add(makeNum(it));
            } else if ((c == '+' || c == '-' || c == '.') && AlphabetHelper.isDigit(lookahead)) {
                // 处理字面量符号前置的情况 如: +5, -5, .5
                Token lastToken = tokens.size() == 0 ? null : tokens.get(tokens.size() - 1);
                if (lastToken == null || !lastToken.isValue()) {
                    it.putBack();
                    tokens.add(makeNum(it));
                } else {
                    if (AlphabetHelper.isOperator(c)) {
                        it.putBack();
                        tokens.add(makeOperator(it));
                    }
                }
            } else if (AlphabetHelper.isOperator(c)) {
                it.putBack();
                tokens.add(makeOperator(it));
            } else if (AlphabetHelper.isSeparator(c)) {
                it.putBack();
                tokens.add(makeSeparator(it));
            } else {
                throw new LexicalException(String.format("The %c is unknown!", c));
            }
        }
        return tokens;
    }

    // 跳过注释
    private void skipComments(PeekCharIterator it) throws LexicalException {
        char c = it.next();
        if (c == '/') {
            while (it.hasNext()) {
                c = it.next();
                if (c == '\n') {
                    break;
                }
            }
        } else if (c == '*') {
            boolean isValid = false;
            while (it.hasNext()) {
                c = it.next();
                if (c == '*' && it.peek() == '/' ) {
                    it.next();
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                throw new LexicalException("The comments grammar is exception.");
            }
        }
    }

    // 解析ID或者关键字
    private Token makeIdOrKeyword(PeekCharIterator it) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            char c = it.next();
            if (!AlphabetHelper.isLiteral(c)) {
                it.putBack();
                break;
            }
            sb.append(c);
        }
        String val = sb.toString();
        if (Keyword.isKeyword(val)) {
            return buildToken(TokenType.KEYWORD, val, it);
        } else if (Keyword.isBoolLiteral(val)) {
            return buildToken(TokenType.BOOL_LITERAL, val, it);
        } else if ("null".equals(val)){
            return buildToken(TokenType.NULL_LITERAL, val, it);
        } else {
            return buildToken(TokenType.IDENTIFIER, val, it);
        }
    }

    // 解析字符串字面量
    private Token makeString(PeekCharIterator it) throws LexicalException {
        StringBuilder sb = new StringBuilder();
        int state = 0;
        while (it.hasNext()) {
            char c = it.next();
            switch (state) {
                case 0:
                    if (c == '\'') {
                        state = 1;
                    } else if (c == '\"'){
                        state = 2;
                    }
                    sb.append(c);
                    break;
                case 1:
                    sb.append(c);
                    if (c == '\'') {
                        return buildToken(TokenType.STRING_LITERAL, sb.toString(), it);
                    }
                    break;
                case 2:
                    sb.append(c);
                    if (c == '\"') {
                        return buildToken(TokenType.STRING_LITERAL, sb.toString(), it);
                    }
                    break;
            }
        }
        throw new LexicalException("Unexpected exception, the String format is error!");
    }

    // 解析操作符
    private Token makeOperator(PeekCharIterator it) throws LexicalException {
        while (it.hasNext()) {
            char c = it.next();
            switch (c) {
                case '+':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.ADD_ASSIGN, "+=", it);
                    } else if (matchNextChar(it, '+')) {
                        return buildToken(TokenType.INC, "++", it);
                    } else {
                        return buildToken(TokenType.ADD, "+", it);
                    }
                case '-':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.SUB_ASSIGN, "-=", it);
                    } else if (matchNextChar(it, '-')) {
                        return buildToken(TokenType.DEC, "--", it);
                    } else {
                        return buildToken(TokenType.SUB, "-", it);
                    }
                case '*':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.MUL_ASSIGN, "*=", it);
                    } else {
                        return buildToken(TokenType.MUL, "*", it);
                    }
                case '/':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.DIV_ASSIGN, "/=", it);
                    } else {
                        return buildToken(TokenType.DIV, "/", it);
                    }
                case '%':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.MOD_ASSIGN, "%=", it);
                    } else {
                        return buildToken(TokenType.MOD, "%", it);
                    }
                case '>':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.GE, ">=", it);
                    } else if (matchNextChar(it, '>')) {
                        return buildToken(TokenType.RSHIFT, ">>", it);
                    } else {
                        return buildToken(TokenType.GT, ">", it);
                    }
                case '<':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.LE, "<=", it);
                    } else if (matchNextChar(it, '<')) {
                        return buildToken(TokenType.LSHIFT, "<<", it);
                    } else {
                        return buildToken(TokenType.LT, "<", it);
                    }
                case '=':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.EQUAL, "==", it);
                    } else {
                        return buildToken(TokenType.ASSIGN, "=", it);
                    }
                case '&':
                    if (matchNextChar(it, '&')) {
                        return buildToken(TokenType.LOGIC_AND, "&&", it);
                    } else if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.AND_ASSIGN, "&=", it);
                    } else {
                        return buildToken(TokenType.BIT_AND, "&", it);
                    }
                case '|':
                    if (matchNextChar(it, '|')) {
                        return buildToken(TokenType.LOGIC_OR, "||", it);
                    } else if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.OR_ASSIGN, "|=", it);
                    } else {
                        return buildToken(TokenType.BIT_OR, "|", it);
                    }
                case '!':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.NOT_EQUAL, "!=", it);
                    } else {
                        return buildToken(TokenType.BANG, "!", it);
                    }
                case '^':
                    if (matchNextChar(it, '=')) {
                        return buildToken(TokenType.XOR_ASSIGN, "^=", it);
                    } else {
                        return buildToken(TokenType.XOR, "^", it);
                    }
                case '~':
                    return buildToken(TokenType.TILDE, "~", it);
            }
        }
        throw new LexicalException("Unexpected Operator");
    }

    // 解析分隔符
    private Token makeSeparator(PeekCharIterator it) throws LexicalException {
        while (it.hasNext()) {
            char c = it.next();
            switch (c) {
                case '(':
                    return buildToken(TokenType.LEFT_PAREN, "(", it);
                case ')':
                    return buildToken(TokenType.RIGHT_PAREN, ")", it);
                case '{':
                    return buildToken(TokenType.LEFT_BRACE, "{", it);
                case '}':
                    return buildToken(TokenType.RIGHT_BRACE, "}", it);
                case '[':
                    return buildToken(TokenType.LEFT_BRACK, "[", it);
                case ']':
                    return buildToken(TokenType.RIGHT_BRACK, "]", it);
                case ',':
                    return buildToken(TokenType.COMMA, ",", it);
                case ';':
                    return buildToken(TokenType.SEMICOLON, ";", it);
                case ':':
                    return buildToken(TokenType.COLON, ":", it);
                case '.':
                    return buildToken(TokenType.DOT, ".", it);
            }
        }
        throw new LexicalException("Unexpected Separator!");
    }

    // 解析数字字面量
    private Token makeNum(PeekCharIterator it) {
        StringBuilder sb = new StringBuilder();
        char c = it.next();
        sb.append(c);
        if (c == '.') { // 处理 .xxx
            processDecNum(it, sb);
            return buildToken(TokenType.FLOAT_LITERAL, sb.toString(), it);
        } else {
            if (c == '+' || c == '-') { // 处理 +xx, -xx
                c = it.next();
                sb.append(c);
            }
            if (c == '0' && (matchNextChar(it, 'x')
                    || matchNextChar(it, 'X'))) {
                sb.append("x");
                processHexNum(it, sb);
                return buildToken(TokenType.HEX_LITERAL, sb.toString(), it);
            } else if (c == '0' && it.peek() != '.') {
                processOctNum(it, sb);
                return buildToken(TokenType.OCT_LITERAL, sb.toString(), it);
            } else {
                boolean isFloat = processDecAndFloatNum(it, sb);
                return buildToken(isFloat ? TokenType.FLOAT_LITERAL : TokenType.DECIMAL_LITERAL, sb.toString(), it);
            }
        }
    }

    private void processHexNum(PeekCharIterator it, StringBuilder sb) {
        while (it.hasNext()) {
            char c = it.next();
            if (!AlphabetHelper.isHexNum(c)) {
                it.putBack();
                break;
            }
            sb.append(c);
        }
    }

    private void processOctNum(PeekCharIterator it, StringBuilder sb) {
        while (it.hasNext()) {
            char c = it.next();
            if (!AlphabetHelper.isOctNum(c)) {
                it.putBack();
                break;
            }
            sb.append(c);
        }
    }

    private boolean processDecAndFloatNum(PeekCharIterator it, StringBuilder sb) {
        boolean isFloat = false;
        processDecNum(it, sb);
        if (matchNextChar(it, '.')) {
            sb.append('.');
            processDecNum(it, sb);
            isFloat = true;
            matchNextChar(it, 'f');
        }
        return isFloat;
    }

    private void processDecNum(PeekCharIterator it, StringBuilder sb) {
        while (it.hasNext()) {
            char c = it.next();
            if (!AlphabetHelper.isDecNum(c)) {
                it.putBack();
                break;
            }
            sb.append(c);
        }
    }

    private boolean matchNextChar(PeekCharIterator it, char c) {
        if (it.peek() == c) {
            it.next();
            return true;
        }
        return false;
    }

    private Token buildToken(TokenType type, String text, PeekCharIterator it) {
        Token token = new Token(type, text);
        token.setLine(it.getLine());
        int start = it.getColumn() - text.length();
        token.setColumn(start > 0 ? start : 0);
        return token;
    }
}
