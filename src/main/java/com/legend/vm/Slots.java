package com.legend.vm;

import com.legend.gen.operand.Register;

/**
 * @author Legend
 * @data by on 21-1-19.
 * @description
 */
public class Slots {

    private Slot[] slots;
    private int slotCount = 0;
    private int offset = 0;

    public Slots(int slotCount) {
        this.slotCount = slotCount;
        if (slotCount > 0) {
            slots = new Slot[slotCount];
            for (int i = 0;i < slotCount;i++) {
                slots[i] = new Slot();
            }
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setInt(int idx, int val) {
        slots[idx + offset].num = val;
        slots[idx + offset].isRef = false;
    }

    public void setInt(Register r1, int val) {
        setInt(r1.getIdx(), val);
    }

    public int getInt(int idx) {
        return (int) slots[idx + offset].num;
    }

    public int getInt(Register r1) {
        return getInt(r1.getIdx());
    }

    public void setBoolean(int idx, boolean val) {
        slots[idx + offset].num = val ? 1 : 0;
        slots[idx + offset].isRef = false;
    }

    public void setBoolean(Register r1, boolean val) {
        setBoolean(r1.getIdx(), val);
    }

    public boolean getBoolean(int idx) {
        return (int) slots[idx + offset].num == 1;
    }

    public boolean getBoolean(Register r1) {
        return getBoolean(r1.getIdx());
    }


    public void setFloat(int idx, float val) {
        slots[idx + offset].num = Float.floatToIntBits(val);
        slots[idx + offset].isRef = false;
    }

    public void setFloat(Register r1, float val) {
        setFloat(r1.getIdx(), val);
    }

    public Float getFloat(int idx) {
        int num = (int) slots[idx + offset].num;
        return Float.intBitsToFloat(num);
    }

    public Float getFloat(Register r1) {
        return getFloat(r1.getIdx());
    }

    public void setLong(int idx, long val) {
        slots[idx + offset].num = val;
        slots[idx + 1 + offset].num = val >> 32;
    }

    public Long getLong(int idx) {
        long low = slots[idx + offset].num;
        long high = slots[idx + 1 + offset].num;
        return low | (high << 32);
    }

    public void setDouble(int idx, double val) {
        long v = Double.doubleToLongBits(val);
        setLong(idx, v);
    }

    public Double getDouble(int idx) {
        return Double.longBitsToDouble(getLong(idx));
    }

    public void setRef(int idx, Object ref) {
        slots[idx + offset].ref = ref;
        slots[idx + offset].isRef = true;
    }

    public boolean isRef(int idx) {
        return slots[idx + offset].isRef;
    }

    public boolean isRef(Register r1) {
        return slots[r1.getIdx()].isRef;
    }

    public void setRef(Register r1, Object ref) {
        setRef(r1.getIdx(), ref);
    }

    public Object getRef(int idx) {
        return slots[idx + offset].ref;
    }

    public Object getRef(Register r1) {
        return getRef(r1.getIdx());
    }

    public int getSize() {
        return slotCount;
    }
}
