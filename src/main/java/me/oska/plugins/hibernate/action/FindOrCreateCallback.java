package me.oska.plugins.hibernate.action;

@FunctionalInterface
public interface FindOrCreateCallback<T> {
    T entity();
}
