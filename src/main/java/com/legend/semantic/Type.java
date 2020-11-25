package com.legend.semantic;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 类型
 */
public interface Type {

    // 类型名称
    String name();

    // 目标类型是否是当前类型
    boolean isType(Type type);
}
