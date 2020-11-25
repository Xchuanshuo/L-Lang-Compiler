package com.legend.parser.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description
 */
public class PriorityTable {

    private List<List<String>> table = new ArrayList<>();

    public PriorityTable() {
        table.add(Arrays.asList("=", "+=", "-=", "*=", "/=",
                "&=", "|=", "^=", "~=", "<<=", ">>=", ">>>="));
        table.add(Collections.singletonList("?"));
        table.add(Collections.singletonList("||"));
        table.add(Collections.singletonList("&&"));
        table.add(Collections.singletonList("|"));
        table.add(Collections.singletonList("^"));
        table.add(Collections.singletonList("&"));
        table.add(Arrays.asList("==", "!="));
        table.add(Arrays.asList("<", "<=", ">", ">=", "is"));
        table.add(Arrays.asList("<<", ">>", ">>>"));
        table.add(Arrays.asList("+", "-"));
        table.add(Arrays.asList("*", "/", "%"));
        table.add(Collections.singletonList("."));
//        table.add(Arrays.asList("!", "+", "~", "++", "--"));
    }

    public int size() {
        return table.size();
    }

    public List<String> getOperator(int level) {
        return table.get(level);
    }

}
