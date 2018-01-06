package com.getset.j7cc.chapter5;

import java.util.Random;

public class MockDocument {
    private static String words[] = {"the", "hello", "goodbye", "packt", "java", "thread", "pool", "random", "class", "main"};

    public static String[][] generateDocument(int numLines, int numWords, String word) {
        String[][] document = new String[numLines][numWords];
        Random random = new Random();
        int count = 0;
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < numWords; j++) {
                document[i][j] = words[random.nextInt(words.length)];
                if (document[i][j].equals(word)) {
                    count++;
                }
            }
        }
        System.out.println("DocumentMock: The word [" + word + "] appears " + count + " times in the document");
        return document;
    }
}
