package org.example.client;

import org.example.dto.VirtualLeadDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SalesforceClient {

    public List<VirtualLeadDTO> findLeads(double minRevenue, double maxRevenue, String province) {
        // 1) si pas de token ou expiré -> authentification REST Salesforce
        // 2) construire une requête SOQL avec les filtres
        // 3) appeler /services/data/.../query?q=...
        // 4) mapper la réponse JSON -> VirtualLeadDTO (ou DTO intermédiaire)


        return Collections.emptyList();
    }

    public List<VirtualLeadDTO> findLeadsByDate(String fromIso, String toIso) {
        return Collections.emptyList();
    }
}
