package com.legend.gen;

import com.legend.ir.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-12-28.
 * @description 字面量常量池
 */
public class GlobalConstantPool {

    private Map<String, Constant> offsetMap = new HashMap<>();
    private List<Constant> constants = new ArrayList<>();
    private int offsetCounter = 0;

    public void add(Constant constant) {
        String value = constant.getType() + "-"
                + String.valueOf(constant.getValue());
        if (!offsetMap.containsKey(value)) {
            constant.setOffset(offsetCounter++);
            offsetMap.put(value, constant);
            constants.add(constant);
        } else {
            Constant c = offsetMap.get(value);
            constant.setOffset(c.getOffset());
        }
    }

    public Constant getByIdx(int idx) {
        return constants.get(idx);
    }

    public int size() {
        return constants.size();
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("ConstantPool-----------------------------------------------");
        for (int i = 0;i < constants.size();i++) {
            list.add(i + ": " + constants.get(i).toString());
        }
        return StringUtils.join(list, "\n");
    }

    public List<Constant> getSymbols() {
        return constants;
    }

}
