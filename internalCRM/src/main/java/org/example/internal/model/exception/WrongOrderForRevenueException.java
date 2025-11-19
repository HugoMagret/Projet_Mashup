package org.example.internal.model.exception;

/**
 * Exception levée lorsque l'ordre des bornes de revenus est incorrect.
 * 
 * Exemple : findLeads(low=150000, high=50000, ...) avec low > high.
 * 
 * La borne basse doit être inférieure ou égale à la borne haute.
 */
public class WrongOrderForRevenueException extends Exception {
    public WrongOrderForRevenueException(String message) { super(message); }
}
