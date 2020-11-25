package com.legend.interpreter;

import com.legend.semantic.Variable;

/**
 * @author Legend
 * @data by on 20-11-20.
 * @description 数组左值
 */
public class ArrayLValue implements LValue {

    private int index = 0;
    private ArrayObject arrayObject;
    private Variable variable;

    public ArrayLValue(Variable variable, ArrayObject obj, int index) {
        this.variable = variable;
        this.arrayObject = obj;
        this.index = index;
    }

    @Override
    public Object getValue() {
        return arrayObject.getValue(index);
    }

    @Override
    public void setValue(Object value) {
        arrayObject.setValue(index, value);
    }

    @Override
    public Variable getVariable() {
        return variable;
    }
}