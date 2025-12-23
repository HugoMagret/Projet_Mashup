package org.example.util;


import org.example.dto.VirtualLeadDTO;
import org.example.internal.InternalLeadDTO;

public class LeadMapper {
    public static VirtualLeadDTO toVirtualLead(InternalLeadDTO internal) {
        VirtualLeadDTO virtual = new VirtualLeadDTO();

        String[] parts = internal.getFirstName().split(",\\s*");
        if (parts.length == 2) {
            virtual.setLastName(parts[0]);
            virtual.setFirstName(parts[1]);
        } else {
            virtual.setFirstName(internal.getFirstName());
            virtual.setLastName("");
        }
        virtual.setCompanyName(internal.getCompanyName());
        virtual.setAnnualRevenue(internal.getAnnualRevenue());
        virtual.setPhone(internal.getPhone());
        virtual.setStreet(internal.getStreet());
        virtual.setPostalCode(internal.getPostalCode());
        virtual.setCity(internal.getCity());
        virtual.setState(internal.getState());
        virtual.setCountry(internal.getCountry());
        virtual.setCreationDate(internal.getCreationDate());





        return virtual;
    }

    /**
     * Convertit un SalesforceLeadDTO en VirtualLeadDTO
     */
    public static VirtualLeadDTO toVirtualLead(org.example.SalesforceLeadDTO salesforce) {
        VirtualLeadDTO virtual = new VirtualLeadDTO();

        // Informations personnelles
        virtual.setFirstName(salesforce.getFirstName() != null ? salesforce.getFirstName() : "");
        virtual.setLastName(salesforce.getLastName() != null ? salesforce.getLastName() : "");

        // Entreprise
        virtual.setCompanyName(salesforce.getCompany() != null ? salesforce.getCompany() : "");

        // Revenus (null-safe)
        if (salesforce.getAnnualRevenue() != null) {
            virtual.setAnnualRevenue(salesforce.getAnnualRevenue());
        } else {
            virtual.setAnnualRevenue(0.0);
        }

        // Contact
        virtual.setPhone(salesforce.getPhone() != null ? salesforce.getPhone() : "");

        // Adresse
        virtual.setStreet(salesforce.getStreet() != null ? salesforce.getStreet() : "");
        virtual.setPostalCode(salesforce.getPostalCode() != null ? salesforce.getPostalCode() : "");
        virtual.setCity(salesforce.getCity() != null ? salesforce.getCity() : "");
        virtual.setState(salesforce.getState() != null ? salesforce.getState() : "");
        virtual.setCountry(salesforce.getCountry() != null ? salesforce.getCountry() : "");

        // Date de création
        virtual.setCreationDate(salesforce.getCreatedDate() != null ? salesforce.getCreatedDate() : "");

        return virtual;
    }
}
