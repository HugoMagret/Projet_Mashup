package org.example.internal.service;

import org.example.internal.InternalCRM;
import org.example.internal.InternalLeadDTO;
import org.example.internal.model.Lead;
import org.example.internal.model.LeadModel;
import org.example.internal.model.LeadModelFactory;
import org.example.internal.utils.ConverterUtils;

import java.util.List;

/**
 * Service Thrift (implémentation) pour InternalCRM.
 *
 * Rôle simple et concret :
 * - recevoir des DTO Thrift (InternalLeadDTO)
 * - convertir en entités métier (Lead) via ConverterUtils
 * - appeler le modèle métier (LeadModel)
 * - attraper les exceptions métier et les retransformer en exceptions Thrift
 */
public class InternalServiceImpl implements InternalCRM.Iface {

    // Modèle métier central (implémentation en mémoire)
    private final LeadModel model = LeadModelFactory.getModel();

    /**
     * findLeads : retourne une liste de InternalLeadDTO correspondant aux critères.
     * Inputs : lowAnnualRevenue, highAnnualRevenue, state (optionnel)
     * Errors : renvoie des exceptions Thrift si le modèle détecte des erreurs
     */
    @Override
    public List<InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue, String state)
            throws org.example.internal.ThriftWrongOrderForRevenueException, org.example.internal.ThriftWrongStateException {
        try {
            List<Lead> leads = model.findLeads(lowAnnualRevenue, highAnnualRevenue, state);
            return ConverterUtils.toDtoList(leads);
        } catch (org.example.internal.model.exception.WrongOrderForRevenueException e) {
            throw new org.example.internal.ThriftWrongOrderForRevenueException(e.getMessage());
        } catch (org.example.internal.model.exception.WrongStateException e) {
            throw new org.example.internal.ThriftWrongStateException(e.getMessage());
        }
    }

    /**
     * findLeadsByDate : retourne les prospects créés entre deux dates ISO.
     * Inputs : startDate, endDate (strings ISO)
     * Errors : renvoie des exceptions Thrift selon les validations du modèle
     */
    @Override
    public List<InternalLeadDTO> findLeadsByDate(String startDate, String endDate)
            throws org.example.internal.ThriftWrongDateFormatException, org.example.internal.ThriftWrongOrderForDateException {
        try {
            java.util.Calendar from = ConverterUtils.isoStringToCalendar(startDate);
            java.util.Calendar to = ConverterUtils.isoStringToCalendar(endDate);
            if (startDate != null && from == null) {
                throw new org.example.internal.ThriftWrongDateFormatException("Format de date invalide : " + startDate);
            }
            if (endDate != null && to == null) {
                throw new org.example.internal.ThriftWrongDateFormatException("Format de date invalide : " + endDate);
            }

            List<Lead> leads = model.findLeadsByDate(from, to);
            return ConverterUtils.toDtoList(leads);
        } catch (org.example.internal.model.exception.WrongDateFormatException e) {
            throw new org.example.internal.ThriftWrongDateFormatException(e.getMessage());
        } catch (org.example.internal.model.exception.WrongOrderForDateException e) {
            throw new org.example.internal.ThriftWrongOrderForDateException(e.getMessage());
        }
    }

    /**
     * createLead : convertit le DTO en Lead et demande au modèle de le créer.
     * Retour : id généré (i64)
     */
    @Override
    public long createLead(InternalLeadDTO lead) throws org.example.internal.ThriftWrongStateException {
        Lead l = ConverterUtils.toModel(lead);
        try {
            return model.createLead(l);
        } catch (org.example.internal.model.exception.WrongStateException e) {
            throw new org.example.internal.ThriftWrongStateException(e.getMessage());
        }
    }

    /**
     * deleteLead : supprime les prospects qui correspondent exactement au DTO fourni.
     * Convertit NoSuchLeadException (modèle) en ThriftNoSuchLeadException.
     */
    @Override
    public void deleteLead(InternalLeadDTO leadDto) throws org.example.internal.ThriftNoSuchLeadException {
        Lead template = ConverterUtils.toModel(leadDto);
        try {
            model.deleteLead(template);
        } catch (org.example.internal.model.exception.NoSuchLeadException e) {
            throw new org.example.internal.ThriftNoSuchLeadException(e.getMessage());
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
                && safeCalEq(a.getCreationDate(), b.getCreationDate())
                && safeEq(a.getCompanyName(), b.getCompanyName())
                && safeEq(a.getState(), b.getState());
    }

    private boolean safeEq(String x, String y) {
        if (x == null) return y == null;
        return x.equals(y);
    }

    private boolean safeCalEq(java.util.Calendar a, java.util.Calendar b) {
        if (a == null) return b == null;
        if (b == null) return false;
        return a.getTimeInMillis() == b.getTimeInMillis();
    }
}
