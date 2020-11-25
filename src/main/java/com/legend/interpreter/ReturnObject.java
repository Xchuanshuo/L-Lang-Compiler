package com.legend.interpreter;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 代表return语句的返回值
 */
public class ReturnObject {

    Object returnValue = null;
    public ReturnObject(Object value) {
        this.returnValue = value;
    }

    @Override
    public String toString() {
        return "ReturnObject";
    }
}
