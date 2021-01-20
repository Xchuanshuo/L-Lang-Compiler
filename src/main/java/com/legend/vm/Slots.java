package com.legend.vm;

/**
 * @author Legend
 * @data by on 21-1-19.
 * @description
 */
public class Slots {

    private Slot[] slots;

    public Slots(int slotCount) {
        if (slotCount > 0) {
            slots = new Slot[slotCount];
            for (int i = 0;i < slotCount;i++) {
                slots[i] = new Slot();
            }
        }
    }

    public void setInt(int idx, int val) {
        slots[idx].num = val;
    }

    public int getInt(int idx) {
        return (int) slots[idx].num;
    }

    public void setFloat(int idx, float val) {
        slots[idx].num = Float.floatToIntBits(val);
    }

    public Float getFloat(int idx) {
        int num = (int) slots[idx].num;
        return Float.intBitsToFloat(num);
    }

    public void setLong(int idx, long val) {
        slots[idx].num = val;
        slots[idx + 1].num = val >> 32;
    }

    public Long getLong(int idx) {
        long low = slots[idx].num;
        long high = slots[idx + 1].num;
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
        slots[idx].ref = ref;
    }

    public Object getRef(int idx) {
        return slots[idx].ref;
    }
}
