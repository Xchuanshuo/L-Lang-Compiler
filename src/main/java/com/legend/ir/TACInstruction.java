package com.legend.ir;

import com.legend.exception.InterpreterException;
import com.legend.semantic.ArrayType;
import com.legend.semantic.Variable;

/**
 * @author Legend
 * @data by on 20-12-7.
 * @description 三地址码 t1 = t2 op t3
 */
public class TACInstruction {

    private Variable result; // 接收地址
    private Object arg1; // 参数1
    private Object arg2; // 参数2
    private String op; // 操作符
    private TACType type; // 指令类型
    private boolean isArrayAssign = false;

    public TACInstruction(TACType type, Variable result,
                          Object arg1, Object arg2, String op) {
        this.type = type;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.op = op;
    }

    public TACInstruction(TACType type) {
        this(type, null, null, null, null);
    }

    public void setArg1(Object arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(Object arg2) {
        this.arg2 = arg2;
    }

    public void setResult(Variable result) {
        this.result = result;
    }

    public Object getArg1() {
        return arg1;
    }

    public Object getArg2() {
        return arg2;
    }

    public String getOp() {
        return op;
    }

    public TACType getType() {
        return type;
    }

    public Variable getResult() {
        return result;
    }

    public boolean isArrayAssign() {
        return isArrayAssign;
    }

    public void setArrayAssign(boolean arrayAssign) {
        isArrayAssign = arrayAssign;
    }

    @Override
    public String toString() {
        switch (type) {
            case ASSIGN:
                if (arg2 != null) {
                    if (isArrayAssign) {
                        return String.format("%s[%s] = %s", result, arg1, arg2);
                    } else if (arg1 instanceof Variable &&
                            ((Variable) arg1).getType() instanceof ArrayType){
                        return String.format("%s = %s[%s]", result, arg1, arg2);
                    } else {
                        return String.format("%s = %s %s %s", result, arg1, op, arg2);
                    }
                } else {
                    return String.format("%s = %s", result, arg1);
                }
            case IF_T:
                return String.format("IF_T %s GOTO %s", arg1, arg2);
            case IF_F:
                return String.format("IF_F %s GOTO %s", arg1, arg2);
            case GOTO:
                return String.format("GOTO %s", arg1.toString().replace(":", ""));
            case LABEL:
                return String.format("%s:", arg1);
            case ENTRY:
                return "FUNC_BEGIN";
            case RETURN:
                return String.format("RETURN %s", arg1 == null ? "" : arg1);
            case ARG:
                return String.format("arg %s", arg1);
            case PARAM:
                return String.format("param %s", arg1);
//            case SP:
//                return String.format("SP %s", arg1);
            case CALL: // 函数有返回值时用临时变量接收
                if (result != null) {
                    return String.format("%s = CALL %s", result, arg1);
                } else {
                    return String.format("CALL %s", arg1);
                }
            case PRINT:
                return String.format("PRINT %s", arg1);
            case CAST_INT:
                return String.format("%s = CAST_INT(%s)", result, arg1);
            case CAST_FLOAT:
                return String.format("%s = CAST_FLOAT(%s)", result, arg1);
            case CAST_BYTE:
                return String.format("%s = CAST_BYTE(%s)", result, arg1);
            case CAST_STR:
                return String.format("%s = CAST_STR(%s)", result, arg1);
            case STR_LEN:
                return String.format("%s = STR_LEN(%s)", result, arg1);
            case NEW_ARRAY:
                return String.format("%s = NEW_ARRAY %s %s", result, arg1, arg2);
        }
        throw new InterpreterException("Unknown opcode type:" + type);
    }
}
