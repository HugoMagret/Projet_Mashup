package org.example.internal.service;

import org.example.internal.InternalCRM;
import org.example.internal.InternalLeadDTO;
import org.example.internal.model.Lead;
import org.example.internal.model.LeadModel;
import org.example.internal.model.LeadModelFactory;
import org.example.internal.model.exception.NoSuchLeadException;
import org.example.internal.utils.ConverterUtils;

import java.util.List;
import java.util.stream.Collectors;

public class InternalServiceImpl implements InternalCRM.Iface {

    // Modèle métier central (implémentation en mémoire)
    private final LeadModel model = LeadModelFactory.getModel();

    /**
     * Trouve les prospects selon une fourchette de revenus et un état (optionnel).
     * Valide l'ordre des bornes et lance une exception Thrift si low > high.
     */
    @Override
    public List<InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue, String state)
            throws org.example.internal.ThriftWrongOrderForRevenueException, org.example.internal.ThriftWrongStateException {
        // Vérification simple : low <= high
        if (lowAnnualRevenue > highAnnualRevenue) {
            throw new org.example.internal.ThriftWrongOrderForRevenueException("La borne basse est supérieure à la borne haute");
        }
        // Vérification d'état (exemple : on n'accepte pas les états vides contenant uniquement des chiffres exotiques)
        if (state != null && state.matches("\\d+")) {
            throw new org.example.internal.ThriftWrongStateException("État invalide fourni : " + state);
        }

        List<Lead> leads = model.findByRevenueRange(lowAnnualRevenue, highAnnualRevenue, state);
        return ConverterUtils.toDtoList(leads);
    }

    /**
     * Trouve les prospects créés entre deux dates ISO.
     * Valide le format (ISO simple) et l'ordre des dates.
     */
    @Override
    public List<InternalLeadDTO> findLeadsByDate(String startDate, String endDate)
            throws org.example.internal.ThriftWrongDateFormatException, org.example.internal.ThriftWrongOrderForDateException {
        // Validation minimale du format ISO (YYYY-...)
        if (startDate != null && !startDate.matches("\\d{4}-.*")) {
            throw new org.example.internal.ThriftWrongDateFormatException("Format de date invalide (ISO attendu) : " + startDate);
        }
        if (endDate != null && !endDate.matches("\\d{4}-.*")) {
            throw new org.example.internal.ThriftWrongDateFormatException("Format de date invalide (ISO attendu) : " + endDate);
        }
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0) {
            throw new org.example.internal.ThriftWrongOrderForDateException("La date de début est après la date de fin");
        }

        List<Lead> leads = model.findByDateRange(startDate, endDate);
        return ConverterUtils.toDtoList(leads);
    }

    /**
     * Crée un nouveau prospect à partir du DTO Thrift.
     * Vérifie simplement que l'état n'est pas constitué uniquement de chiffres.
     */
    @Override
    public long createLead(InternalLeadDTO lead) throws org.example.internal.ThriftWrongStateException {
        if (lead == null) throw new org.example.internal.ThriftWrongStateException("Lead vide");
        if (lead.getState() != null && lead.getState().matches("\\d+")) {
            throw new org.example.internal.ThriftWrongStateException("État invalide fourni : " + lead.getState());
        }
        Lead l = ConverterUtils.toModel(lead);
        return model.createLead(l);
    }

    /**
     * Supprime les prospects qui correspondent exactement au DTO fourni.
     * Si aucun prospect trouvé, lance ThriftNoSuchLeadException.
     */
    @Override
    public void deleteLead(InternalLeadDTO leadDto) throws org.example.internal.ThriftNoSuchLeadException {
        Lead l = ConverterUtils.toModel(leadDto);
        List<Lead> candidates = model.findByRevenueRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
        boolean removed = false;
        for (Lead candidate : candidates) {
            if (equalsWithoutId(candidate, l)) {
                try {
                    model.deleteLead(candidate.getId());
                    removed = true;
                } catch (NoSuchLeadException e) {
                    // si quelqu'un a déjà supprimé, on continue
                }
            }
        }
        if (!removed) {
            throw new org.example.internal.ThriftNoSuchLeadException("Aucun prospect correspondant trouvé pour suppression");
        }
    }

    // Compare deux leads sur tous les champs sauf l'id
    private boolean equalsWithoutId(Lead a, Lead b) {
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

    private boolean safeEq(String x, String y) {
        if (x == null) return y == null;
        return x.equals(y);
    }
}
