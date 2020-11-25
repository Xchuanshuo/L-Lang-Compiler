package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.parser.common.PeekTokenIterator;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 函数类型
 */
public class FunctionType extends ASTNode {

    public FunctionType() {
        this.astNodeType = ASTNodeType.FUNCTION_TYPE;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        FunctionType functionType = new FunctionType();
        it.nextMatch(Keyword.Key.FUNCTION);
        ASTNode typeTypeOrVoid = TypeTypeOrVoid.parse(it);
        functionType.addChild(typeTypeOrVoid); // 返回值类型
        it.nextMatch("(");
        functionType.addChild(TypeList.parse(it)); // 参数类型列表
        it.nextMatch(")");
        return functionType;
    }

    public TypeTypeOrVoid typeTypeOrVoid() {
        return getASTNode(TypeTypeOrVoid.class);
    }

    public TypeList typeList() {
        return getASTNode(TypeList.class);
    }
}
