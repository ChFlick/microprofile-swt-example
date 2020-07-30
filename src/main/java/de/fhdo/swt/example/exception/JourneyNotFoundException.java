package de.fhdo.swt.example.exception;

public class JourneyNotFoundException extends RuntimeException {
    public JourneyNotFoundException() {
        super();
    }

    public JourneyNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JourneyNotFoundException(final String message) {
        super(message);
    }

    public JourneyNotFoundException(final Throwable cause) {
        super(cause);
    }
}