package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 数组初始化
 */
public class ArrayInitializer extends ASTNode {

    public ArrayInitializer() {
        this.astNodeType = ASTNodeType.ARRAY_INITIALIZER;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ArrayInitializer arrayInitializer = new ArrayInitializer();
        it.nextMatch("{");
        while (it.hasNext() && !it.peek().getText().equals("}")) {
            ASTNode vi = VariableInitializer.parse(it);
            arrayInitializer.addChild(vi);
            if (!it.peek().getText().equals("}")) {
                it.nextMatch(",");
            }
        }
        it.nextMatch("}");
        return arrayInitializer;
    }

    public List<VariableInitializer> variableInitializerList() {
        return getASTNodes(VariableInitializer.class);
    }
}
