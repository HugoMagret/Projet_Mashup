package org.example.internal.model.exception;

/**
 * Exception levée lorsque la valeur du champ "state" (région/département) est invalide.
 * 
 * Exemple : si state contient uniquement des chiffres (validation actuelle),
 * cette exception sera levée. À adapter selon les règles métier.
 */
public class WrongStateException extends Exception {
    public WrongStateException(String message) { super(message); }
}
