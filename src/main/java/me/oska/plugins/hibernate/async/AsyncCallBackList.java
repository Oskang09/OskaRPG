package me.oska.plugins.hibernate.async;

import java.util.List;

@FunctionalInterface
public interface AsyncCallBackList<T> {
    void done(List<T> results);
}
