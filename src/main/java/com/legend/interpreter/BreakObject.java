package com.legend.interpreter;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 用于代表一个break语句的对象
 */
public class BreakObject {

    private static BreakObject instance = new BreakObject();

    private BreakObject() {}

    // 获取唯一的实例
    public static BreakObject instance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Break";
    }
}
