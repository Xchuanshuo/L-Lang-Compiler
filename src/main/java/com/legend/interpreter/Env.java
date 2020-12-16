package com.legend.interpreter;

import com.legend.semantic.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 用来保存对象运行时的数据
 */
public class Env {

    // 成员变量
    protected Map<Variable, Object> fields = new HashMap<>();

    public Object getValue(Variable variable) {
        Object rtn = fields.get(variable);
        if (rtn == null) {
            rtn = NullObject.instance();
        }
        return rtn;
    }

    public void setValue(Variable variable, Object value) {
        fields.put(variable, value);
    }
}
