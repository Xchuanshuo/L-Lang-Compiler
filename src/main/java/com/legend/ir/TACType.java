package com.legend.ir;

/**
 * @author Legend
 * @data by on 20-12-7.
 * @description 三地址码类型
 */
public enum TACType {
    IF_T, // if true goto label
    IF_F, // if false goto label
    GOTO, // goto label
    ASSIGN, // t1 = t2
    LABEL, // Label l1
    CALL, // call f
    ARG, // arg 1(实参)
    PARAM, // param 1(形参)
    RETURN, // return (expr)
    ENTRY, // entry 程序入口位置

    // 内置函数
    INPUT,
    PRINT,
    READ,
    WRITE,
    CAST_INT,
    CAST_FLOAT,
    CAST_STR,
    CAST_BYTE,
    STR_LEN,
    STR_AT,

    NEW_INSTANCE,
    INVOKE_SPECIAL,
    INVOKE_VIRTUAL,
    INVOKE_STATIC,
    GET_FIELD,
    GET_STATIC_FIELD,
    NEW_ARRAY, // new_array type n
    ARRAY_LEN, // ARRAY_LEN arrObj
}
