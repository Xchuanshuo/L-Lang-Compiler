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
    ENTRY, // entry localSize(启动时分配栈的大小) 程序入口

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

    NEW_INSTANCE, // 创建一个类实例对象
    NEW_FUNC_OBJ, // 创建一个函数对象
    INVOKE_SPECIAL, // 调用构造方法
    INVOKE_VIRTUAL, // 调用普通实例方法
    INVOKE_STATIC, // 调用静态方法
    INVOKE_VAR_FUNC, // 调用函数变量的实际函数
    GET_FIELD, // 获取类普通成员字段
    PUT_FIELD, // 设置...字段
    GET_STATIC_FIELD, // 获取类静态字段
    PUT_STATIC_FIELD, // 设置类静态字段
    GET_MODULE_VAR,  // 获取模块(全局)变量
    PUT_MODULE_VAR, // 设置模块变量
    GET_UPVALUE_VAR, // 获取自由变量
    PUT_UPVALUE_VAR, // 设置自由变量
    NEW_ARRAY, // new_array type n
    ARRAY_LEN, // ARRAY_LEN arrObj
}
