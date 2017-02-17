package com.github.kahalemakai.safely;

import java.util.concurrent.Callable;

public class Safely {

    /* ************************************************************
     *                       static methods                       *
     * ***********************************************************/

    public static Runnable wrapRunnable(Runnable r) throws WrappedException {
        return () -> {
            try {
                r.run();
            }
            catch (Throwable e) {
                throw new WrappedException(e);
            }
        };
    }

    public static <T> SafeCallable<T> wrapCallable(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            }
            catch (Throwable e) {
                throw new WrappedException(e);
            }
        };
    }

    public static <T> T call(Callable<T> callable) {
        return wrapCallable(callable).call();
    }

    public static <T> SafeCallable<T> uncheckedCallable(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            }
            catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new WrappedException(e);
            }
        };
    }

    public static <T> T callUnchecked(Callable<T> callable) {
        return uncheckedCallable(callable).call();
    }

    public static Runnable silenty(Runnable r) {
        return () -> {
            try {
                r.run();
            } catch (Throwable ignore) { }
        };
    }

}

