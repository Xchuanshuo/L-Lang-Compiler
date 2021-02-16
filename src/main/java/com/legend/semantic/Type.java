package com.legend.semantic;

import java.io.Serializable;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description 类型
 */
public interface Type extends Serializable {

    long serialVersionUID = -5809782578272943999L;

    // 类型名称
    String name();

    // 目标类型是否是当前类型
    boolean isType(Type type);
}
