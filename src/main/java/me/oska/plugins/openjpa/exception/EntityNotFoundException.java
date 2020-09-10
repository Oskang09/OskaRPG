package me.oska.plugins.openjpa.exception;

public class EntityNotFoundException extends RunicException {

    public EntityNotFoundException(Class<?> type) {
        super("[Hibernate] " + type.getName() + " could not be found!", "");
    }
}