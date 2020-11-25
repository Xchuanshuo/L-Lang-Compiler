package com.legend.parser.common;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.TokenType;
import com.legend.parser.ast.ASTNode;
import com.legend.parser.ast.TypeType;
import com.legend.parser.common.PeekTokenIterator;

import static com.legend.lexer.Keyword.Key.VOID;
import static java.lang.Enum.valueOf;

/**
 * @author Legend
 * @data by on 20-11-13.
 * @description 工具类 用于判断句子的类型
 */
public class PeekUtils {

    public static boolean isFuncDeclaration(PeekTokenIterator it) throws ParseException {
        int pos = it.getPosition();
        if (it.peek().getText().equals(Keyword.getValueByKey(VOID))) {
            return true;
        }
        boolean isFunc = false;
        ASTNode typeNode = TypeType.parse(it);
        if (typeNode == null) {
            it.putBackByPosition(pos);
        }
        boolean isConstructor = false;
        if (it.topIsEqual("(")) {
            it.putBackByPosition(pos);
            isConstructor = true;
        }
        if (it.topIsEqual(TokenType.IDENTIFIER)) {
            it.nextMatch(TokenType.IDENTIFIER);
            // 构造方法需要和函数调用区分开
            if (isConstructor) {
                // 需要先判断不是.表达式的函数调用
                if (!it.topIsEqual(TokenType.DOT)) {
                    it.nextMatch("(");
                    while (it.hasNext() && !it.topIsEqual(")")) {
                        it.next();
                    }
                    it.nextMatch(")");
                    if (it.topIsEqual("{")) {
                        isFunc = true;
                    }
                }
            } else {
                if (it.topIsEqual("(")) {
                    isFunc = true;
                }
            }
        }
        it.putBackByPosition(pos);
        return isFunc;
    }

    public static boolean isVariableDeclaration(PeekTokenIterator it) throws ParseException {
        int pos = it.getPosition();
        boolean flag = false;
        ASTNode typeType = TypeType.parse(it);
        if (typeType == null) {
            it.putBackByPosition(pos);
            return false;
        }
        if (it.topIsEqual(TokenType.IDENTIFIER)) {
            it.nextMatch(TokenType.IDENTIFIER);
            // todo
            if (!it.topIsEqual("(")) {
                flag = true;
            }
//            if (it.hasNext()) {
//                String text = it.peek().getText();
//                System.out.println(text);
//                if (text.equals("=") || text.equals(";") || text.equals(",")){
//                    flag = true;
//                }
//            }
        }
        it.putBackByPosition(pos);
        return flag;
    }
}
