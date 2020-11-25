package com.legend.interpreter;

import com.legend.semantic.Variable;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 对栈中值的引用 用来获取变量的值以及地址
 */
public interface LValue {

    Object getValue();

    void setValue(Object value);

    Variable getVariable();
}
