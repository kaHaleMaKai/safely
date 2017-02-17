package com.github.kahalemakai.safely;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * A wrapper exception that delegates most methods
 * to the wrapped exception.
 * <p>
 * Only a single constructor is offered, as {@code WrappedException}
 * should only be used as safety means to encapsulate
 * other (potentially checked) exceptions; thus only a
 * {@link Throwable throwable cause} is needed to construct
 * an instance.
 * <p>
 * Additionally, it offers a few convenience methods.
 */
public final class WrappedException extends RuntimeException {
    private final boolean causedByInterruptedException;
    @Getter
    private Throwable wrappedException;

    /**
     * Construct a new instance.
     * @param cause the cause
     */
    public WrappedException(Throwable cause) {
        super(cause);
        this.wrappedException = cause;
        this.causedByInterruptedException = (cause instanceof InterruptedException);
    }

    /**
     * Test whether the wrapped exception is an instance of a given class.
     * @param exceptionClass
     *     the exception to test
     * @return
     *     whether the wrapped exception is an instance of a given class
     */
    public boolean wraps(@NonNull Class<? extends Exception> exceptionClass) {
        return wrappedException.getClass().equals(exceptionClass);
    }

    /**
     * Interrupt the current thread if the wrapped
     * exception is a {@link InterruptedException InterruptedException}.
     * @return
     *     {@code true} if the current thread has been interrupted, else {@code false}
     */
    public boolean interruptIfNecessary() {
        if (causedByInterruptedException) {
            Thread.currentThread().interrupt();
            return true;
        }
        return false;
    }

    /**
     * Use the wrapped exception as cause to a new exception.
     * <p>
     * This is just a short-cut for {@code new SomeException(e.getWrappedException())}.
     * @param exceptionClass
     *     type of new top-level exception
     * @param <T>
     *     type of new top-level exception
     * @return
     *     the new top-level exception
     */
    public <T extends Throwable> T rethrowable(Class<? extends T> exceptionClass) {
        try {
            val ex = exceptionClass.newInstance();
            ex.initCause(wrappedException);
            return ex;
        } catch (IllegalAccessException | InstantiationException ignore) {
        }
        try {
            val constructor = exceptionClass.getConstructor(Throwable.class);
            try {
                return constructor.newInstance(wrappedException);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) { }
        } catch (NoSuchMethodException e) {
            try {
                val constructor = exceptionClass.getConstructor(String.class, Throwable.class);
                try {
                    return constructor.newInstance("", wrappedException);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) { }
            } catch (NoSuchMethodException e1) {
                try {
                    val constructor = exceptionClass.getConstructor(String.class);
                    try {
                        val ex = constructor.newInstance("");
                        ex.initCause(wrappedException);
                        return ex;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) { }
                } catch (NoSuchMethodException ignore) { }
            }
        }
        throw new UnsupportedOperationException("cannot instantiate instance of type " + exceptionClass.getCanonicalName(), this);
    }

    /**
     * Use the wrapped exception as cause to a new exception, and specify a message.
     * <p>
     * This is just a short-cut for {@code new SomeException(e.getWrappedException())}.
     * @param msg
     *     the new error message
     * @param exceptionClass
     *     type of new top-level exception
     * @param <T>
     *     type of new top-level exception
     * @return
     *     the new top-level exception
     */
    public <T extends Throwable> T rethrowable(String msg, Class<? extends T> exceptionClass) {
        try {
            val constructor = exceptionClass.getConstructor(String.class, Throwable.class);
            try {
                return constructor.newInstance(msg, wrappedException);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) { }
        } catch (NoSuchMethodException e1) {
            try {
                val constructor = exceptionClass.getConstructor(String.class);
                try {
                    val ex = constructor.newInstance(msg);
                    ex.initCause(wrappedException);
                    return ex;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) { }
            } catch (NoSuchMethodException ignore) { }
        }
        throw new UnsupportedOperationException("cannot instantiate instance of type " + exceptionClass.getCanonicalName(), this);
    }

    /* ************************************************************
     *                         delegates                          *
     * ***********************************************************/

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public String getMessage() {
        return wrappedException.getMessage();
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public String getLocalizedMessage() {
        return wrappedException.getLocalizedMessage();
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public synchronized Throwable getCause() {
        return wrappedException.getCause();
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return wrappedException.initCause(cause);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public void printStackTrace() {
        wrappedException.printStackTrace();
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public void printStackTrace(PrintStream s) {
        wrappedException.printStackTrace(s);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        wrappedException.printStackTrace(s);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public StackTraceElement[] getStackTrace() {
        return wrappedException.getStackTrace();
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to the wrapped exception.
     */
    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        wrappedException.setStackTrace(stackTrace);
    }

}

