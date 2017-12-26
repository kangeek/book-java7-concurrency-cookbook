package com.getset.j7cc.chapter3;

/**
 * 在array内保存被查找的数字在矩阵的每行里出现的次数。
 */
public class Results {
    private int data[];
    public Results(int size) {
        data = new int[size];
    }
    public void setData(int position, int value) {
        data[position] = value;
    }
    public int[] getData() {
        return data;
    }
}
