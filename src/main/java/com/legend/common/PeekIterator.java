package com.legend.common;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 数据流迭代器
 */
public class PeekIterator<T> implements Iterator<T> {

    private static final int CACHE_CAPACITY = 20;
    private LinkedList<T> cacheQueue = new LinkedList<>();
    private Stack<T> putBackStack = new Stack<>();
    private Iterator<T> it;
    private T endToken = null;
    private int count = 0; // 计数器,用来迭代

    private int line = 1, column = 1;

    public PeekIterator(T[] array) {
        this(Arrays.asList(array), null);
    }

    public PeekIterator(T[] array, T endToken) {
        this(Arrays.asList(array), endToken);
    }

    public PeekIterator(List<T> list) {
        this(list.stream(), null);
    }

    public PeekIterator(List<T> list, T endToken) {
        this(list.stream(), endToken);
    }

    public PeekIterator(Stream<T> stream) {
        this(stream, null);
    }

    public PeekIterator(Stream<T> stream, T endToken) {
        this.it = stream.iterator();
        this.endToken = endToken;
    }

    public PeekIterator(Iterator<T> it, T endToken) {
        this.it = it;
        this.endToken = endToken;
    }

    public T peek() {
        if (putBackStack.size() > 0) {
            return putBackStack.peek();
        }
        if (!it.hasNext()) {
            return endToken;
        }
        T val = next();
        putBack();
        return val;
    }

    public void putBack() {
        if (cacheQueue.size() > 0) {
            putBackStack.push(cacheQueue.pollLast());
            count--;
        }
    }

    @Override
    public boolean hasNext() {
        return it.hasNext() || putBackStack.size() > 0;
    }

    @Override
    public T next() {
        T val = null;
        if (putBackStack.size() > 0) {
            val = putBackStack.pop();
        } else {
            if (!it.hasNext()) {
                T temp = endToken;
                endToken = null;
                return temp;
            }
            val = it.next();
        }
        count++;
        while (cacheQueue.size() >= CACHE_CAPACITY) {
            cacheQueue.pollFirst();
        }
        cacheQueue.offerLast(val);
        return val;
    }

    public int getPosition() {
        return count;
    }

    public void putBackByPosition(int position) {
        for (int i = count;i > position;i--) {
            putBack();
        }
        count = position;
    }


    public Stack<T> getPutBackStack() {
        return putBackStack;
    }

    public LinkedList<T> getCacheQueue() {
        return cacheQueue;
    }
}
