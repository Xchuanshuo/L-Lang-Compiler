package com.legend.lexer;

import com.legend.common.PeekIterator;


/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 字符迭代器
 */
public class PeekCharIterator extends PeekIterator<Character> {

    private int line = 1;
    private int column = 1;
    private int lastColumn = 1;

    public PeekCharIterator(Character[] array, Character endToken) {
        super(array, endToken);
    }

    @Override
    public void putBack() {
        super.putBack();
        if (!getPutBackStack().isEmpty() && getPutBackStack().peek() == '\n') {
            line--;
            column = lastColumn;
        } else {
            column--;
        }
    }

    @Override
    public Character next() {
        char c = super.next();
        if (c == '\n') {
            line++;
            lastColumn = column;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
