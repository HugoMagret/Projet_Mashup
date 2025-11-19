package org.example.internal.model.exception;

/**
 * Exception levée lorsque l'on tente de supprimer ou accéder à un prospect
 * qui n'existe pas dans le CRM.
 * 
 * Exemple : deleteLead() n'a trouvé aucun prospect correspondant au template fourni.
 */
public class NoSuchLeadException extends Exception {
    public NoSuchLeadException(String message) { super(message); }
}
