package com.legend.vm;

import com.legend.gen.MethodArea;
import com.legend.semantic.Class;
import com.legend.semantic.PrimitiveType;

/**
 * @author Legend
 * @data by on 21-1-24.
 * @description string池
 */
public class StringPool {

    public static Object getStrObj(String value) {
        Class clazz = (Class) MethodArea.getInstance().
                getTypeByName(PrimitiveType.String.name());
        Object strObj = clazz.newObj();
        strObj.setData(value);
        return strObj;
    }

    public static String getString(Object object) {
        return object.toString();
    }
}
