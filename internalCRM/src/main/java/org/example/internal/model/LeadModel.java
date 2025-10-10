package org.example.internal.model;

import org.example.internal.model.exception.NoSuchLeadException;
import org.example.internal.model.exception.WrongDateFormatException;
import org.example.internal.model.exception.WrongOrderForDateException;
import org.example.internal.model.exception.WrongOrderForRevenueException;
import org.example.internal.model.exception.WrongStateException;

import java.util.List;
import java.util.Calendar;

/**
 * Interface du modèle métier pour gérer les prospects (Lead).
 * Les validations robustes (format/date/order/state) sont réalisées dans
 * l'implémentation et peuvent lancer des exceptions métier Java.
 */
public interface LeadModel {

    /* Retourne la liste des leads dont le chiffre d'affaires est entre low et high
       et, si state non null/empty, appartenant à cet état/département.
       Peut lancer : WrongOrderForRevenueException, WrongStateException */
    List<Lead> findLeads(double low, double high, String state)
            throws WrongOrderForRevenueException, WrongStateException;

    /**
     * Retourne la liste des leads créés entre from et to (Calendar).
     * Peut lancer : WrongDateFormatException (si null inattendu), WrongOrderForDateException
     */
    List<Lead> findLeadsByDate(Calendar from, Calendar to)
            throws WrongDateFormatException, WrongOrderForDateException;

    // Crée un nouveau lead et retourne son identifiant unique.
    // Peut lancer : WrongStateException
    long createLead(Lead lead) throws WrongStateException;

    // Supprime les leads correspondant au template fourni.
    // Lance NoSuchLeadException si aucun lead supprimé.
    void deleteLead(Lead template) throws NoSuchLeadException;
}
