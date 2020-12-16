package com.legend.interpreter;

import com.legend.semantic.BlockScope;
import com.legend.semantic.Scope;
import com.legend.semantic.Variable;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 栈帧
 */
public class StackFrame {

    // 该Frame所对应的scope
    Scope scope = null;

    /**
     * 如果是同一级函数调用,跟上一级的parentFrame相同
     * 如果是下一级的函数调用或for、If等block,parentFrame是自己
     * 如果是一个闭包, 那么要带一个存放在堆里的环境
     */
    StackFrame parentFrame = null;

    // 实际存放变量的地方(保存了作用域在内的变量,以及自由变量)
    Env object = null;

    public StackFrame(BlockScope scope) {
        this.scope = scope;
        this.object = new Env();
    }

    public StackFrame(ClassObject object) {
        this.scope = object.type;
        this.object = object;
    }

    public StackFrame(FunctionObject object) {
        this.scope = object.function;
        this.object = object;
    }

    protected boolean contains(Variable variable) {
        if (object != null && object.fields != null) {
            return object.fields.containsKey(variable);
        }
        return false;
    }

    @Override
    public String toString() {
        String rtn = "" + scope;
        if (parentFrame != null) {
            rtn += " -> " + parentFrame;
        }
        return rtn;
    }
}

