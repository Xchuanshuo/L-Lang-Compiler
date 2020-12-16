package com.legend.interpreter;

import com.legend.semantic.Variable;


public class MyLValue implements LValue {
    private Variable variable;
    private Env container;

    public MyLValue(Env container, Variable variable) {
        this.container = container;
        this.variable = variable;
    }

    @Override
    public Object getValue() {
        if (variable instanceof Variable.This ||
                variable instanceof Variable.Super) {
            return container;
        }
        return container.getValue(variable);
    }

    @Override
    public void setValue(Object value) {
        container.fields.put(variable, value);
        if (value instanceof FunctionObject) {
            ((FunctionObject) value).receiver = variable;
        }
    }

    @Override
    public Variable getVariable() {
        return variable;
    }
}
