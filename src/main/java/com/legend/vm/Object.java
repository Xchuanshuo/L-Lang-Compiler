package com.legend.vm;

import com.legend.exception.LVMException;
import com.legend.semantic.Class;
import com.legend.semantic.Function;
import com.legend.semantic.Type;

public class Object {

    private Type type;
    private java.lang.Object data;

    public Object(Class clazz) {
        this.type = clazz;
        this.data = new Slots(clazz.getFieldCount());
    }

    public Object(Class clazz,java.lang.Object data) {
        this.type = clazz;
        this.data = data;
    }

    public Object(Function function) {
        this.type = function;
        this.data = new Slots(function.getClosureMaxSize());
    }

    public void setData(java.lang.Object data) {
        this.data = data;
    }

    public void copyAndSet(Slots newSlots) {
        Slots oldSlots = (Slots) data;
        if (oldSlots.getSize() > newSlots.getSize()) {
            throw new LVMException("The size of newSlots is" +
                    "greater than the size of oldSlots!");
        }
        int size = oldSlots.getSize();
        for (int i = 0;i < size;i++) {
            if (oldSlots.isRef(i)) {
                newSlots.setRef(i, oldSlots.getRef(i));
            } else {
                newSlots.setInt(i, oldSlots.getInt(i));
            }
        }
        this.data = newSlots;
    }

    public Class clazz() {
        return (Class) type;
    }

    public Type type() {
        return type;
    }

    public Function function() {
        return (Function) type;
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

    public Slots upValueSlots() {
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
        return String.valueOf(data).replace("Slots", type.name());
    }
}
