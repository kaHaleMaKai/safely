package com.github.kahalemakai.safely;

import java.util.concurrent.Callable;
import java.util.function.Function;

@FunctionalInterface
public interface SafeCallable<T> {
    T call();

    default Callable<T> asCallable() {
        return this::call;
    }

    default Runnable asRunnable() {
        return this::call;
    }

    default <S> SafeCallable<S> andThen(Function<T, S> fn) {
        return Safely.wrapCallable(() -> fn.apply(call()));
    }

    default SafeCallable<T> onError(Callable<T> callable) {
        return () -> {
            try {
                return call();
            } catch (Throwable e) {
                return Safely.call(callable);
            }
        };
    }

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
