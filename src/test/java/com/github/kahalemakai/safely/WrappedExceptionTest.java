package com.github.kahalemakai.safely;

import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class WrappedExceptionTest {

    @Test
    public void wraps() throws Exception {
        val ex1 = new WrappedException(new InterruptedException());
        assertEquals(InterruptedException.class, ex1.getWrappedException().getClass());
        val ex2 = new WrappedException(new IOException());
        assertEquals(IOException.class, ex2.getWrappedException().getClass());
        assertTrue(ex1.wraps(InterruptedException.class));
        assertTrue(ex2.wraps(IOException.class));
    }

    @Test
    public void interrupted() throws Exception {
        val ex1 = new WrappedException(new IOException());
        assertFalse(Thread.currentThread().isInterrupted());
        assertFalse(ex1.interruptIfNecessary());
        assertFalse(Thread.currentThread().isInterrupted());
        val ex2 = new WrappedException(new InterruptedException());
        assertFalse(Thread.currentThread().isInterrupted());
        assertTrue(ex2.interruptIfNecessary());
        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    @Test
    public void delegates() throws Exception {
        val runEx = new RuntimeException();
        val ioEx = new IOException("io exception", runEx);
        val ex = new WrappedException(ioEx);
        assertEquals(ioEx.getMessage(), ex.getMessage());
        assertArrayEquals(ioEx.getStackTrace(), ex.getStackTrace());
        assertEquals(ioEx.getLocalizedMessage(), ex.getLocalizedMessage());
        assertEquals(ioEx.getCause(), ex.getCause());
    }

    @Test
    public void rethrowableStringAndException() throws Exception {
        val ex = new WrappedException(new IOException("io msg", new IllegalAccessException()));
        val msg = "this is really bad";
        val ex1 = ex.rethrowable(msg, NoSuchElementException.class);
        assertEquals(NoSuchElementException.class, ex1.getClass());
        assertEquals(IOException.class, ex1.getCause().getClass());
        assertEquals("io msg", ex1.getCause().getMessage());
        assertEquals(IllegalAccessException.class, ex1.getCause().getCause().getClass());
        assertEquals(msg, ex1.getMessage());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void rethrowableStringAndExceptionThrows() throws Exception {
        val ex = new WrappedException(new RuntimeException());
        ex.rethrowable("this is really, really bad!", BadException.class);
    }


    @Test
    public void rethrowableExceptionOnly() throws Exception {
        val ex = new WrappedException(new IOException("io msg", new IllegalAccessException()));
        val ex1 = ex.rethrowable(NoSuchElementException.class);
        assertEquals(NoSuchElementException.class, ex1.getClass());
        assertEquals(IOException.class, ex1.getCause().getClass());
        assertEquals("io msg", ex1.getCause().getMessage());
        assertEquals(IllegalAccessException.class, ex1.getCause().getCause().getClass());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void rethrowableExceptionOnlyThrows() throws Exception {
        val ex = new WrappedException(new RuntimeException());
        ex.rethrowable(BadException.class);
    }

    private static class BadException extends Exception {
        public BadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
