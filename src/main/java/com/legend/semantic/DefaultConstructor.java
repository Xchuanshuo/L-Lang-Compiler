package com.legend.semantic;

/**
 * @author Legend
 * @data by on 20-11-14.
 * @description
 */
public class DefaultConstructor extends Function {

    protected DefaultConstructor(String name, Scope enclosingScope) {
        super(name, enclosingScope, null);
    }

    public Class Class() {
        return (Class) enclosingScope;
    }
}
