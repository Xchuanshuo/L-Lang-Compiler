package com.legend.vm;

import com.legend.gen.MethodArea;
import com.legend.parser.ast.ASTNode;
import com.legend.semantic.Class;
import com.legend.semantic.PrimitiveType;

/**
 * @author Legend
 * @data by on 21-1-24.
 * @description 内建类
 */
public class BuiltInClass {

    public static void init() {
        MethodArea.getInstance().addType(PrimitiveType.String.name(), new _String());
    }

    public static class _String extends Class {

        public _String() {
            super("String", null);
            setParentClass(rootClass);
        }
    }


}
