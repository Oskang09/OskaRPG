package me.oska.plugins.hibernate.async;

import me.oska.plugins.hibernate.exception.RunicException;

@FunctionalInterface
public interface AsyncCallBackExceptionHandler {
    void error(RunicException e);
}