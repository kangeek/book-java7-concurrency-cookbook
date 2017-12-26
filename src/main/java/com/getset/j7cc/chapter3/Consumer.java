package com.getset.j7cc.chapter3;

import java.util.List;
import java.util.concurrent.Exchanger;

public class Consumer implements Runnable {
    private List<String> buffer;
    private final Exchanger<List<String>> exchanger;

    public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        int cycle = 1;
        for (int i = 0; i < 10; i++) {
            System.out.println("<<< Consumer: Cycle " + cycle);

            try {
                /**
                 * Producer调用exchange后会休眠，Consumer调用exchange会交换数据并唤醒Producer。
                 */
                buffer = exchanger.exchange(buffer);
                System.out.println("<-> Consumer buffer size: " + buffer.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 显示交换过来的数据
            for (int j = 0; j < 10; j++) {
                String message = buffer.get(0);
                System.out.println("- Consumer: " + message);
                buffer.remove(0);
            }
            cycle++;
        }
    }
}
