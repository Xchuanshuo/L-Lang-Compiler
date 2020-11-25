package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 类型列表节点
 */
public class TypeList extends ASTNode {

    public TypeList() {
        this.astNodeType = ASTNodeType.TYPE_LIST;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        TypeList typeList = new TypeList();
        ASTNode typeType = null;
        while ((typeType = TypeType.parse(it)) != null) {
            typeList.addChild(typeType);
            if (!it.peek().getText().equals(",")) {
                break;
            }
            it.nextMatch(",");
        }
        return typeList;
    }

    public List<TypeType> typeTypeList() {
        return getASTNodes(TypeType.class);
    }
}
