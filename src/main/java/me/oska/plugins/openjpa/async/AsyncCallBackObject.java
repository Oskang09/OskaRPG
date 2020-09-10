package me.oska.plugins.openjpa.async;

@FunctionalInterface
public interface AsyncCallBackObject<T> {
    void done(T result);
}
