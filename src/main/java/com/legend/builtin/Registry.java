package com.legend.builtin;

import com.legend.builtin.core._String;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Legend
 * @data by on 21-2-3.
 * @description 内建函数注册中心
 */
public class Registry {

    public static final String DEFAULT = "core";

    private static Map<String, BuiltInFunction> map = new HashMap<>();

    public static void initBuiltIn() {
        _String.bind();
    }

    public static void register(String clazz, String descriptor,
                                BuiltInFunction function) {
        String key = clazz + "_" + descriptor;
        map.put(key, function);
    }

    public static BuiltInFunction findBuiltin(String key) {
        return map.get(key);
    }
}
