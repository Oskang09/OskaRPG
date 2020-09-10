package me.oska.plugins.logger;

@FunctionalInterface
public interface Tracker {
    void track() throws Exception;
}
