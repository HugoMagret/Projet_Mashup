package org.example;

import org.example.internal.InternalLeadDTO;

import java.util.Collections;
import java.util.List;

/**
 * Implémentation "vide" qui ne renvoie aucun lead Salesforce.
 *
 * À remplacer plus tard par une vraie implémentation qui appelle Salesforce.
 */
public class StubSalesforceLeadProvider implements SalesforceLeadProvider {

    @Override
    public List<InternalLeadDTO> fetchAllLeadsAsInternalDto() {
        // TODO: implémenter l'appel REST à Salesforce + mapping
        // Pour l'instant, on renvoie une liste vide pour tester le reste.
        return Collections.emptyList();
    }
}

