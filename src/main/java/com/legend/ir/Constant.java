package com.legend.ir;

import com.legend.semantic.Symbol;
import com.legend.semantic.Type;

/**
 * @author Legend
 * @data by on 20-12-7.
 * @description 常量
 */
public class Constant extends Symbol {

    private Object value;
    private Type type = null;

    public Constant(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        if (value.equals("\n")) {
            value = "\\n";
        }
        return "$" + value + "";
    }
}
