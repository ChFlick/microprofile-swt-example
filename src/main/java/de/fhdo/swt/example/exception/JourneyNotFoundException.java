package de.fhdo.swt.example.exception;

import javax.ws.rs.NotFoundException;

public class JourneyNotFoundException extends NotFoundException {
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