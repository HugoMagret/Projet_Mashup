package org.example.client.service.utils.exceptions;


@SuppressWarnings("serial")
public class InputValidationException extends Exception {

    public InputValidationException(String message) {
        super(message);
    }
}