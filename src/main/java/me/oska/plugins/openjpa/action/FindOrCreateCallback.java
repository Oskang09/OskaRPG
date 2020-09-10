package me.oska.plugins.openjpa.action;

@FunctionalInterface
public interface FindOrCreateCallback<T> {
    T entity();
}
