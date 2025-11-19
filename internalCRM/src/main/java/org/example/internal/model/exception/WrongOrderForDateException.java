package org.example.internal.model.exception;

/**
 * Exception levée lorsque l'ordre des dates est incorrect dans une recherche.
 * 
 * Exemple : findLeadsByDate(from, to) avec from > to (date début après date fin).
 * 
 * La validation est faite dans LeadModelImpl avec Calendar.after().
 */
public class WrongOrderForDateException extends Exception {
    public WrongOrderForDateException(String message) { super(message); }
}
