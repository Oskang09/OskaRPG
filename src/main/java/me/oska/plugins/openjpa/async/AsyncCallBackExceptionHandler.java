package me.oska.plugins.openjpa.async;

import me.oska.plugins.openjpa.exception.RunicException;

@FunctionalInterface
public interface AsyncCallBackExceptionHandler {
    void error(RunicException e);
}