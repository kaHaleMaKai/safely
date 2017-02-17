package com.github.kahalemakai.safely;

import java.util.concurrent.Callable;

/**
 * A utility class for safely wrapping lambdas to deal with both
 * checked and unchecked exceptions.
 */
public final class Safely {
    private Safely() {
        throw new SecurityException("com.github.kahalemakai.safely.Safely cannot be instantiated");
    }

    /* ************************************************************
     *                       static methods                       *
     * ***********************************************************/

    /**
     * Wrap any {@link Throwable throwable} a {@link Runnable runnable} might throw
     * into a {@link WrappedException WrappedException}.
     * @param r the {@code Runnable} to wrap
     * @return the wrapped {@code Runnable}
     */
    public static Runnable wrapRunnable(Runnable r) {
        return () -> {
            try {
                r.run();
            }
            catch (Throwable e) {
                throw new WrappedException(e);
            }
        };
    }

    /**
     * Wrap a {@link Callable callable} such that it only throws
     * {@link WrappedException WrappedExceptions} on {@link Callable#call()}.
     * @param callable the {@code Callable} to wrap
     * @param <T> type of return value of the wrapped {@code Callable}
     * @return the wrapped {@code Callable}
     */
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

    /**
     * Call a {@link Callable callable}, safely wrapped using {@link #wrapCallable(Callable)}.
     * @param callable the {@code Callable} to wrap
     * @param <T> type of return value of the wrapped {@code Callable}
     * @return the result of executing {@code Callable#call()}
     *
     * @see Safely#wrapCallable(Callable)
     */
    public static <T> T call(Callable<T> callable) {
        return wrapCallable(callable).call();
    }

    /**
     * Wrap a {@link Callable callable} such that all checked exceptions are wrapped
     * into {@link WrappedException WrappedExceptions} when applying {@link Callable#call()}.
     * @param callable the {@code Callable} to wrap
     * @param <T> type of return value of the wrapped {@code Callable}
     * @return the wrapped {@code Callable}
     */
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

    /**
     * Call a {@link Callable callable}, safely wrapped using {@link #uncheckedCallable(Callable)}.
     * @param callable the {@code Callable} to wrap
     * @param <T> type of return value of the wrapped {@code Callable}
     * @return the result of executing {@code Callable#call()}
     *
     * @see Safely#wrapCallable(Callable)
     */
    public static <T> T callUnchecked(Callable<T> callable) {
        return uncheckedCallable(callable).call();
    }

    /**
     * Wrap a {@link Runnable runnable} such that all (unchecked) exceptions
     * thrown under execution of {@link Runnable#run()} are silenced.
     * @param r the {@code Runnable} to wrap
     * @return the wrapped {@code Runnable}
     */
    public static Runnable silently(Runnable r) {
        return () -> {
            try {
                r.run();
            } catch (Throwable ignore) { }
        };
    }

    /**
     * Execute a {@link Runnable runnable's} {@code run()} method
     * and silence all exceptions by wrapping it using
     * {@link #silently(Runnable)}.
     * @param r the {@code Runnable} to wrap
     */
    public static void runSilently(Runnable r) {
        silently(r).run();
    }

}

