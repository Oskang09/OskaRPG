package me.oska.plugins.hibernate.exception;

public abstract class RunicException extends Exception {

    private String detailedMessage;

    RunicException(String message, String detailedMessage) {
        super(message);
        this.detailedMessage = detailedMessage;
    }

    private String getDetailedMessage() {
        return detailedMessage;
    }

    @Override
    public String getMessage() {
        return this.getMessage();
    }
}