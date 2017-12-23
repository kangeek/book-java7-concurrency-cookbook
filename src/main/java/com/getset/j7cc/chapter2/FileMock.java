package com.getset.j7cc.chapter2;

import java.util.Random;

public class FileMock {
    private String content[];
    private int index;

    // 用随机字符初始化文件的内容。
    public FileMock(int size, int length) {
        content = new String[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                int indice = 'a' + random.nextInt(26);
                buffer.append((char) indice);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    public boolean hasMoreLines() {
        return index < content.length;
    }

    public String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length - index));
            return content[index++];
        }
        return null;
    }
}
