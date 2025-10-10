package org.example.internal;

import java.util.*;
import org.example.internal.ThriftNoSuchLeadException;
import org.example.internal.ThriftWrongDateFormatException;
import org.example.internal.ThriftWrongOrderForDateException;
import org.example.internal.ThriftWrongOrderForRevenueException;
import org.example.internal.ThriftWrongStateException;


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
    /**
     * Handler utilisé par le serveur et la demo.
     * Il délègue au modèle métier et convertit/propage les exceptions Thrift.
     */

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
    // initialisation en string -> converti lors de createLead via ConverterUtils
    exemple.setCreationDate("2024-09-01T10:00:00Z");
        try {
            createLead(exemple);
        } catch (ThriftWrongStateException e) {
            // Ignorer pour la démo si l'initialisation échoue
        }
    }

    @Override
    public List<org.example.internal.InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue, String province)
            throws ThriftWrongOrderForRevenueException, ThriftWrongStateException {
        // Appel direct au modèle. Les erreurs sont converties en exceptions Thrift.
        try {
            List<org.example.internal.model.Lead> leads = model.findLeads(lowAnnualRevenue, highAnnualRevenue, province);
            return org.example.internal.utils.ConverterUtils.toDtoList(leads);
        } catch (org.example.internal.model.exception.WrongOrderForRevenueException e) {
            throw new ThriftWrongOrderForRevenueException(e.getMessage());
        } catch (org.example.internal.model.exception.WrongStateException e) {
            throw new ThriftWrongStateException(e.getMessage());
        }
    }

    @Override
    public List<org.example.internal.InternalLeadDTO> findLeadsByDate(String fromIso, String toIso)
            throws ThriftWrongDateFormatException, ThriftWrongOrderForDateException {
        try {
            java.util.Calendar from = org.example.internal.utils.ConverterUtils.isoStringToCalendar(fromIso);
            java.util.Calendar to = org.example.internal.utils.ConverterUtils.isoStringToCalendar(toIso);
            if (fromIso != null && from == null) throw new ThriftWrongDateFormatException("Format de date invalide: " + fromIso);
            if (toIso != null && to == null) throw new ThriftWrongDateFormatException("Format de date invalide: " + toIso);
            List<org.example.internal.model.Lead> leads = model.findLeadsByDate(from, to);
            return org.example.internal.utils.ConverterUtils.toDtoList(leads);
        } catch (org.example.internal.model.exception.WrongDateFormatException e) {
            throw new ThriftWrongDateFormatException(e.getMessage());
        } catch (org.example.internal.model.exception.WrongOrderForDateException e) {
            throw new ThriftWrongOrderForDateException(e.getMessage());
        }
    }

    @Override
    public long createLead(org.example.internal.InternalLeadDTO lead) throws ThriftWrongStateException {
        org.example.internal.model.Lead l = org.example.internal.utils.ConverterUtils.toModel(lead);
        try {
            return model.createLead(l);
        } catch (org.example.internal.model.exception.WrongStateException e) {
            throw new ThriftWrongStateException(e.getMessage());
        }
    }

    @Override
    public void deleteLead(org.example.internal.InternalLeadDTO leadDto) throws ThriftNoSuchLeadException {
        org.example.internal.model.Lead template = org.example.internal.utils.ConverterUtils.toModel(leadDto);
        try {
            model.deleteLead(template);
        } catch (org.example.internal.model.exception.NoSuchLeadException e) {
            throw new ThriftNoSuchLeadException(e.getMessage());
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
                && safeCalEq(a.getCreationDate(), b.getCreationDate())
                && safeEq(a.getCompanyName(), b.getCompanyName())
                && safeEq(a.getState(), b.getState());
    }

    private boolean safeEq(String x, String y) { if (x == null) return y == null; return x.equals(y); }

    private boolean safeCalEq(java.util.Calendar a, java.util.Calendar b) {
        if (a == null) return b == null;
        if (b == null) return false;
        return a.getTimeInMillis() == b.getTimeInMillis();
    }
}
