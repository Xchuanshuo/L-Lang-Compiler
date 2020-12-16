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
    ENTRY, // entry f (函数或方法声明)
    EXIT, // exit (退出函数)

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

    NEW_ARRAY, // new_array type n
}
