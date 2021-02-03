package com.legend.builtin;

import com.legend.builtin.core._String;
import com.legend.common.MetadataArea;
import com.legend.semantic.PrimitiveType;

/**
 * @author Legend
 * @data by on 21-1-24.
 * @description 内建类
 */
public class BuiltInClass {

    public static void init() {
        MetadataArea.getInstance().addType(PrimitiveType.String.name(), new _String());
    }


}
