package com.github.kahalemakai.safely;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public final class WrappedException extends RuntimeException {
    private final boolean causedByInterruptedException;
    @Getter
    private Throwable wrappedException;

    public WrappedException(Throwable cause) {
        super(cause);
        this.wrappedException = cause;
        this.causedByInterruptedException = (cause instanceof InterruptedException);
    }

    public boolean wraps(@NonNull Class<? extends Exception> exception) {
        return wrappedException.getClass().equals(exception);
    }

    public boolean interruptIfNecessary() {
        if (causedByInterruptedException) {
            Thread.currentThread().interrupt();
            return true;
        }
        return false;
    }

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

    @Override
    public String getMessage() {
        return wrappedException.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return wrappedException.getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return wrappedException.getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return wrappedException.initCause(cause);
    }

    @Override
    public void printStackTrace() {
        wrappedException.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        wrappedException.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        wrappedException.printStackTrace(s);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return wrappedException.getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        wrappedException.setStackTrace(stackTrace);
    }

}

