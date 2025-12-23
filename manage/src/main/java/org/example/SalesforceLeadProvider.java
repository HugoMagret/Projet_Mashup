package org.example;

import org.example.internal.InternalLeadDTO;

import java.util.List;

/**
 * Abstraction pour fournir des Leads venant de Salesforce.
 *
 * L'idée : cette interface renvoie directement des InternalLeadDTO prêts
 * à être passés à createLead() du service InternalCRM.
 *
 * Tu pourras plus tard fournir une implémentation réelle qui :
 *   - appelle l'API REST Salesforce,
 *   - mappe le JSON vers InternalLeadDTO.
 */
public interface SalesforceLeadProvider {

    /**
     * Récupère TOUS les Leads Salesforce, convertis en InternalLeadDTO.
     */
    List<InternalLeadDTO> fetchAllLeadsAsInternalDto() throws Exception;
}

