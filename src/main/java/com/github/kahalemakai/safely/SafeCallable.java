package com.github.kahalemakai.safely;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * A replacement for {@link Callable} that does not throw
 * checked exceptions (cmp. {@link Callable#call()}).
 * <p>
 * Additionally, methods for chaining calls are provided, 
 * and basic error handling.
 * @param <T> type of value returned by {@link #call()}.
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #call()}.
 */
public interface SafeCallable<T> {

    /**
     * Computes a result.
     *
     * @return computed result
     */
    T call();

    /**
     * Cast {@code this} instance to {@link Callable Callable}.
     * @return {@code this} as {@code Callable}
     */
    default Callable<T> asCallable() {
        return this::call;
    }

    /**
     * Cast {@code this} instance to {@link Runnable Runnable}.
     * @return {@code this} as {@code Runnable}
     */
    default Runnable asRunnable() {
        return this::call;
    }

    /**
     * Compose a {@code SafeCallable} with a {@link Function function}
     * to return a new {@code SafeCallable}.
     * @param fn
     *     the {@code Function} that transforms {@code this} {@code Callable's} output
     * @param <S>
     *     type of return value of the transformation function
     * @return
     *     composition of {@code this} with the given function
     */
    default <S> SafeCallable<S> andThen(Function<T, S> fn) {
        return Safely.wrapCallable(() -> fn.apply(call()));
    }

    /**
     * Add an error handler to be called if {@link #call()} fails.
     * <p>
     * The {@link Callable callable} error handler will be wrapped
     * using {@link Safely#call(Callable)}.
     * @param callable
     *     the {@code Callable} to be called in case on an exception
     * @return
     *     {@code this} {@code SafeCallabe} with attached error handler
     */
    default SafeCallable<T> onError(Callable<T> callable) {
        return () -> {
            try {
                return call();
            } catch (Throwable e) {
                return Safely.call(callable);
            }
        };
    }

    /**
     * In case {@link #call()} fails, return a static value instead.
     * @param value
     *     the value to be returned in case of failure
     * @return
     *     {@code this} {@code SafeCallabe} with attached error handler
     */
    default SafeCallable<T> onErrorReturn(T value) {
        return () -> {
            try {
                return call();
            } catch (Throwable e) {
                return value;
            }
        };
    }


}
