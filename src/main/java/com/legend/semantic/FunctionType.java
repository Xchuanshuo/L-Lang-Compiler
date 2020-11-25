package com.legend.semantic;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 函数类型
 */
public interface FunctionType extends Type {

    // 返回值类型
    Type returnType();

    // 参数类型列表
    List<Type> paramTypeList();

    boolean matchParameterTypes(List<Type> paramTypes);
}
