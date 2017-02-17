package com.github.kahalemakai.safely;

import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class SafelyTest {

    @Test
    public void wrapRunnable() throws Exception {
        val counter = new AtomicInteger();
        final Runnable r = counter::getAndIncrement;
        Safely.wrapRunnable(r).run();
        assertEquals(1, counter.get());
        final Runnable r2 = () -> {
            counter.getAndIncrement();
            throw new RuntimeException("hello");
        };
        try {
            Safely.wrapRunnable(r2).run();
            throw new AssertionError("didn't catch an exception");
            
        } catch (WrappingException e) {
            assertEquals(2, counter.get());
            assertEquals("hello", e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void uncheckCallableRethrowsUncheckedException() throws Exception {
        Safely.uncheckedCallable(() -> { throw new IllegalArgumentException();}).call();
    }

    @Test(expected = WrappingException.class)
    public void callUncheckedThrowsWrappedException() throws Exception {
        Safely.callUnchecked(() -> { throw new IOException();});
    }

    @Test(expected = IllegalArgumentException.class)
    public void callUncheckedThrows() throws Exception {
        Safely.callUnchecked(() -> { throw new IllegalArgumentException();});
    }

    @Test(expected = WrappingException.class)
    public void unckechedCallableThrowsWrappedException() throws Exception {
        Safely.uncheckedCallable(() -> { throw new IOException();}).call();
    }

    @Test
    public void wrapCallable() throws Exception {
        final Callable<Integer> c = () -> 42;
        val s = Safely.wrapCallable(c);
        assertEquals(42, (int) s.call());
        assertEquals(42, (int) Safely.call(c));
        final Callable<Integer> c2 = () -> {
            throw new IOException("your luck");
        };
        val s2 = Safely.wrapCallable(c2);
        try {
            s2.call();
            throw new AssertionError("didn't catch an exception");
        }
        catch (WrappingException e) {
            assertEquals("your luck", e.getMessage());
        }
        try {
            Safely.call(c2);
            throw new AssertionError("didn't catch an exception");
        }
        catch (WrappingException e) {
            assertEquals("your luck", e.getMessage());
        }
    }

    @Test
    public void silently() throws Exception {
        val counter = new AtomicInteger();
        Runnable r = () -> {
            counter.getAndIncrement();
            throw new IllegalArgumentException();
        };
        Safely.silently(r).run();
        assertEquals(1, counter.get());
        Safely.runSilently(r);
        assertEquals(2, counter.get());
    }
}