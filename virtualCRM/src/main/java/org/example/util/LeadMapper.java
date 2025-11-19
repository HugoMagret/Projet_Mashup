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
}
