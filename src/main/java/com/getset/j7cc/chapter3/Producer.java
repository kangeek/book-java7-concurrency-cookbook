package com.getset.j7cc.chapter3;

import java.util.List;
import java.util.concurrent.Exchanger;

public class Producer implements Runnable {
    private List<String> buffer;
    private final Exchanger<List<String>> exchanger;

    public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        int cycle = 1;
        /**
         * 循环10遍，每遍产生10个字符串放到buffer里边
         */
        for (int i = 0; i < 10; i++) {
            System.out.println(">>> Producer: Cycle " + cycle);
            for (int j = 0; j < 10; j++) {
                String message = "Event " + ((i * 10) + j);
                System.out.println("+ Producer: " + message);
                buffer.add(message);
            }

            try {
                /**
                 * 调用 exchange() 方法来与consumer交换数据。此方法可能会抛出InterruptedException 异常, 加上处理代码。
                 * 第一个调用 exchange()方法会进入休眠直到其他线程的达到。
                  */
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("<-> Producer buffer size: " + buffer.size());
            cycle++;
        }
    }
}
