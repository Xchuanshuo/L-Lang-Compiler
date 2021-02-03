package com.legend.vm;

import com.legend.common.MetadataArea;
import com.legend.semantic.Class;
import com.legend.semantic.PrimitiveType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Legend
 * @data by on 21-1-24.
 * @description string池
 */
public class StringPool {

    private static Map<String,Object> map = new HashMap<>();

    public static Object getStrObj(String value) {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        Class clazz = (Class) MetadataArea.getInstance().
                getTypeByName(PrimitiveType.String.name());
        Object strObj = clazz.newObj();
        strObj.setData(value);
        map.put(value, strObj);
        return strObj;
    }

    public static String getString(Object object) {
        return ((String) object.getData());
    }
}
