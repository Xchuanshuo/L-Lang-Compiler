package com.legend.gen.operand;

/**
 * @author Legend
 * @data by on 20-9-11.
 * @description 标签
 */
public class Label extends Offset {

    private String label;

    public Label(String label) {
        super(0);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getOffset() + " #" + label;
    }
}
