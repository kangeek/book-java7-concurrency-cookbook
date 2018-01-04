package com.getset.j7cc.chapter4;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class FactorialCalculator implements Callable<Integer> {
    private Integer number;

    public FactorialCalculator(Integer number) {
        this.number = number;
    }

    @Override
    public Integer call() throws Exception {
        if (number == 0 || number == 1) {
            return 1;
        } else {
            int result = 1;
            for (int i = 1; i < number; i++) {
                result *= i;
                TimeUnit.SECONDS.sleep(1);
            }
            return result;
        }
    }
}
