package org.example.internal.model.exception;

/**
 * Exception levée lorsqu'une date fournie ne respecte pas le format ISO-8601 attendu.
 * 
 * Format attendu : "yyyy-MM-dd'T'HH:mm:ss'Z'" (exemple : "2024-09-15T10:00:00Z")
 * 
 * Lancée par le service lors du parsing d'une chaîne ISO invalide.
 */
public class WrongDateFormatException extends Exception {
    public WrongDateFormatException(String message) { super(message); }
}
