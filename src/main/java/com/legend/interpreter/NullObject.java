package com.legend.interpreter;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 用来表示null值的对象
 */
public class NullObject extends ClassObject {

    private static NullObject instance = new NullObject();

    private NullObject() {}

    public static NullObject instance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Null";
    }
}
