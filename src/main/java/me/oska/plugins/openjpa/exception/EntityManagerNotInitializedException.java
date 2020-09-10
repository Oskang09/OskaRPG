package me.oska.plugins.openjpa.exception;

public class EntityManagerNotInitializedException extends RunicException {

    public EntityManagerNotInitializedException() {
        super("[Hibernate] EntityManager could not be initialized!", "");
    }
}