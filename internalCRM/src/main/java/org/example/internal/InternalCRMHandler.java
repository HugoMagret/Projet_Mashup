package org.example.internal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * CRM INTERNE : stocke et filtre les prospects commerciaux en mémoire
 * 
 * QUE FAIT CE SERVICE :
 *   - Garde une liste de prospects (nom, entreprise, chiffre d'affaires, région...)
 *   - Trouve les prospects par critères (revenus entre X et Y, région donnée)
 *   - Trouve les prospects par date de création
 *   - Ajoute/supprime des prospects
 * 
 * EXEMPLE D'USAGE :
 *   findLeads(50000, 150000, "Loire-Atlantique") 
 *   → tous les prospects entre 50k€ et 150k€ en Loire-Atlantique
 */
public class InternalCRMHandler implements InternalCRM.Iface {

    // Déléguer vers le modèle métier centralisé
    private final org.example.internal.model.LeadModel model = org.example.internal.model.LeadModelFactory.getModel();

    public InternalCRMHandler() {
        // Initialiser avec un exemple pour les démos (même comportement qu'avant)
        org.example.internal.InternalLeadDTO exemple = new org.example.internal.InternalLeadDTO();
        exemple.setFirstName("Jean");
        exemple.setLastName("Dupont");
        exemple.setCompanyName("Acme");
        exemple.setAnnualRevenue(50000.0);
        exemple.setPhone("+33123456789");
        exemple.setStreet("1 rue Exemple");
        exemple.setPostalCode("49100");
        exemple.setCity("Angers");
        exemple.setState("Maine-et-Loire");
        exemple.setCountry("France");
        exemple.setCreationDate("2024-09-01T10:00:00Z");
        createLead(exemple);
    }

    @Override
    public List<org.example.internal.InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue, String province) {
        List<org.example.internal.model.Lead> leads = model.findByRevenueRange(lowAnnualRevenue, highAnnualRevenue, province);
        return org.example.internal.utils.ConverterUtils.toDtoList(leads);
    }

    @Override
    public List<org.example.internal.InternalLeadDTO> findLeadsByDate(String fromIso, String toIso) {
        List<org.example.internal.model.Lead> leads = model.findByDateRange(fromIso, toIso);
        return org.example.internal.utils.ConverterUtils.toDtoList(leads);
    }

    @Override
    public long createLead(org.example.internal.InternalLeadDTO lead) {
        org.example.internal.model.Lead l = org.example.internal.utils.ConverterUtils.toModel(lead);
        return model.createLead(l);
    }

    @Override
    public void deleteLead(org.example.internal.InternalLeadDTO leadDto) {
        // Supprimer par correspondance de champs (même logique que InternalServiceImpl)
        org.example.internal.model.Lead template = org.example.internal.utils.ConverterUtils.toModel(leadDto);
        List<org.example.internal.model.Lead> all = model.findByRevenueRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
        for (org.example.internal.model.Lead candidate : all) {
            if (equalsWithoutId(candidate, template)) {
                try {
                    model.deleteLead(candidate.getId());
                } catch (org.example.internal.model.exception.NoSuchLeadException e) {
                    // ignore
                }
            }
        }
    }

    private boolean equalsWithoutId(org.example.internal.model.Lead a, org.example.internal.model.Lead b) {
        if (a == null || b == null) return false;
        return safeEq(a.getFirstName(), b.getFirstName()) && safeEq(a.getLastName(), b.getLastName())
                && Double.compare(a.getAnnualRevenue(), b.getAnnualRevenue()) == 0
                && safeEq(a.getPhone(), b.getPhone())
                && safeEq(a.getStreet(), b.getStreet())
                && safeEq(a.getPostalCode(), b.getPostalCode())
                && safeEq(a.getCity(), b.getCity())
                && safeEq(a.getCountry(), b.getCountry())
                && safeEq(a.getCreationDate(), b.getCreationDate())
                && safeEq(a.getCompanyName(), b.getCompanyName())
                && safeEq(a.getState(), b.getState());
    }

    private boolean safeEq(String x, String y) { if (x == null) return y == null; return x.equals(y); }
}
