package com.legend.builtin;

import com.legend.vm.Slots;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Legend
 * @data by on 21-2-2.
 * @description
 */
public class BuiltInFunction {

    private Object object;
    private String funcName;

    public BuiltInFunction(Object o, String funcName) {
        this.object = o;
        this.funcName = funcName;
    }

    public void invoke(Slots stack, Slots regs) {
        try {
            Method method = object.getClass().getMethod(funcName, stack.getClass(), regs.getClass());
            method.invoke(object, stack ,regs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
