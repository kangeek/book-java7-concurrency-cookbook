package com.getset.j7cc.chapter3;

import java.util.Random;

/**
 * 随机生成一个在1-10之间的 数字矩阵，我们将从中查找数字。
 */
public class MatrixMock {
    private int data[][];
    public MatrixMock(int size, int length, int number) {
        int count = 0;
        data = new int[size][length];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                data[i][j] = random.nextInt(10);
                if (data[i][j] == number) {
                    count++;
                }
            }
        }
        System.out.println("Mock: There are " + count + " ocurrences of " + number + " in generated data.");
    }

    public int[] getRow(int row) {
        if ((row >= 0) && (row < data.length)) {
            return data[row];
        }
        return null;
    }
}
