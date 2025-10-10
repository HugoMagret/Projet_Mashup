package org.example.internal.model;

import java.util.List;

/**
 * Interface du modèle métier pour gérer les prospects (Lead).
 * Implémentation attendue : opérations de lecture/écriture simples.
 */
public interface LeadModel {
    Lead findById(long id) throws org.example.internal.model.exception.NoSuchLeadException;
    
    /* Retourne la liste des leads dont le chiffre d'affaires est entre low et high
    et, si state non null/empty, appartenant à cet état/département. */
    List<Lead> findByRevenueRange(double low, double high, String state);
    
    /**
     * Retourne la liste des leads créés entre fromIso et toIso (format ISO simple).
     */
    List<Lead> findByDateRange(String fromIso, String toIso);
    
    // Crée un nouveau lead et retourne son identifiant unique.
    long createLead(Lead lead);

    // Supprime le lead identifié par id.
    void deleteLead(long id) throws org.example.internal.model.exception.NoSuchLeadException;
}
