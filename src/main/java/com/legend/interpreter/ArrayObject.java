package com.legend.interpreter;

import com.legend.semantic.ArrayType;

import java.util.*;

/**
 * @author Legend
 * @data by on 20-11-19.
 * @description 数组对象
 */
public class ArrayObject {

    // 数组类型
    private ArrayType type;
    private Object[] objects;
    private Map<Long, Long> dimensionMap = new TreeMap<>();
    private int offset = 0;

    public ArrayObject() {}

    public ArrayObject(ArrayType type) {
        this.type = type;
    }

    public ArrayObject(ArrayType type, int size) {
        this.type = type;
        this.objects = new Object[size];
    }

    public void setType(ArrayType type) {
        this.type = type;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public void setObjects(Object[] objects, int offset) {
        this.objects = objects;
        this.offset = offset;
    }

    public Object[] getObjects() {
        return objects;
    }

    public Object getValue(int index) {
        return objects[offset + index];
    }

    public void setValue(int index, Object value) {
        objects[index+offset] = value;
    }

    public void setLength(long dimension, long len) {
        dimensionMap.put(dimension, len);
    }

    public long getLength(long dimension) {
        return dimensionMap.getOrDefault(dimension, 0L);
    }

    public int getDimension() {
        return dimensionMap.size();
    }

    public Map<Long, Long> getDimensionMap() {
        return dimensionMap;
    }

    public void setDimensionMap(Map<Long, Long> dimensionMap) {
        this.dimensionMap = dimensionMap;
    }

    public long getSize() {
        return objects.length;
    }

    // 获取到当前维度的元素个数
    public long getCount(int d, long idx) {
        return getCount(dimensionMap, d, idx);
    }

    // 获取到当前维度的元素个数
    public long getCount(Map<Long, Long> map, int d, long idx) {
        int j = 0;
        for (Map.Entry<Long, Long> entry : map.entrySet()) {
            if (j > d) {
                idx *= entry.getValue();
            }
            j += 1;
        }
        return idx;
    }


    public ArrayObject getArrayObject(int dimension, int idx) {
        if (dimension > dimensionMap.size() || idx > getSize()) {
            System.out.println("数组越界");
            return null;
        }
        // a[1] = 1 , a= {1,1,1,1,1}
        // a[1][1] = 1*2 + 1, a = {{1,1}, {2,2}}
        // a[1][1][1] = 1*2*3 + 1*3 + 1, a = {{{1,1,1},{1,1,2}}, {{1,1,1}, {2,2,2}}} (a[2][2][3])
        // 1.达到数组维度取数时 直接返回数组元素 a(i,j,k...n) = i*j*k*..n + j * k *..n + n
        // 2.未达到维度时返回数组对象 a[1][1] 1*2*3 + 1*3
        ArrayObject newObject = new ArrayObject();
        Map<Long, Long> oldMap = getDimensionMap();
        int j = 0, i = 0;
        for (Map.Entry<Long, Long> entry : oldMap.entrySet()) {
            if (j > dimension) {
                newObject.setLength(i++, entry.getValue());
            }
            j += 1;
        }
        newObject.setObjects(getObjects(), idx);
        return newObject;
    }
}
