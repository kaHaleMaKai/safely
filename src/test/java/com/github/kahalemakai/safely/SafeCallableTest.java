package com.github.kahalemakai.safely;

import lombok.val;
import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public class SafeCallableTest {
    @Test
    public void onError() throws Exception {
        final SafeCallable<Integer> s = (() -> {
            throw new IllegalArgumentException();
        });
        final SafeCallable<Integer> s2 = () -> 23;
        assertEquals(42, (int) s.onError(() -> 42).call());
        assertEquals(43, (int) s.onErrorReturn(43).call());
        assertEquals(23, (int) s2.onError(() -> 42).call());
    }

    @Test
    public void init() throws Exception {
        final SafeCallable<Integer> s = () -> 23;
        assertEquals(23, (int) call(s.asCallable()));
        run(s.asRunnable());
    }

    @Test
    public void andThen() throws Exception {
        final SafeCallable<Integer> s = () -> 23;
        final SafeCallable<Double> d = s.andThen(i -> i / 2.);
        assertEquals(11.5, d.call(), 1e-6);
        val s2 = d.andThen(t -> {
            if (false) {
                return 5;
            }
            throw new IllegalArgumentException();
        }).onErrorReturn(42);
        assertEquals(42, (int) s2.call());
    }

    private <T> T call(Callable<T> callable) throws Exception {
        return callable.call();
    }

    private void run(Runnable r) {
        r.run();
    }

}