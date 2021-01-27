package com.legend.vm;

import com.legend.semantic.Class;

public class Object {

    private Class clazz;
    private java.lang.Object data;

    public Object(Class clazz) {
        this.clazz = clazz;
        this.data = new Slots(clazz.getFieldCount());
    }

    public Object(Class clazz,java.lang.Object data) {
        this.clazz = clazz;
        this.data = data;
    }

    public void setData(java.lang.Object data) {
        this.data = data;
    }

    public Class clazz() {
        return clazz;
    }

    public byte[] bytes() {
        return (byte[]) data;
    }

    public int[] ints() {
        return (int[]) data;
    }

    public boolean[] booleans() {
        return (boolean[]) data;
    }

    public float[] floats() {
        return (float[]) data;
    }

    public Object[] objs() {
        return (Object[]) data;
    }

    public Slots fieldSlots() {
        return (Slots) data;
    }

    public int arrayLength() {
        if (data instanceof byte[]) {
            return ((byte[]) data).length;
        } else if (data instanceof boolean[]) {
            return ((boolean[]) data).length;
        } else if (data instanceof int[]) {
            return ((int[]) data).length;
        } else if (data instanceof float[]) {
            return ((float[]) data).length;
        } else if (data instanceof Object[]) {
            return ((Object[]) data).length;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(data).replace("Slots", clazz.getName());
    }
}
