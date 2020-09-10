package me.oska.plugins.hibernate.async;

@FunctionalInterface
public interface AsyncCallBackObject<T> {
    void done(T result);
}
