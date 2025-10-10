package org.example.internal.utils;

import org.example.internal.InternalLeadDTO;
import org.example.internal.model.Lead;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire de conversion entre le modèle métier `Lead` et le DTO Thrift `InternalLeadDTO`.
 * Les méthodes sont simples et copies les champs un par un.
 */
public final class ConverterUtils {
    private ConverterUtils() {}

    // Convertit un objet métier en DTO Thrift
    public static InternalLeadDTO toDto(Lead lead) {
        InternalLeadDTO dto = new InternalLeadDTO();
        dto.setFirstName(lead.getFirstName());
        dto.setLastName(lead.getLastName());
        dto.setAnnualRevenue(lead.getAnnualRevenue());
        dto.setPhone(lead.getPhone());
        dto.setStreet(lead.getStreet());
        dto.setPostalCode(lead.getPostalCode());
        dto.setCity(lead.getCity());
        dto.setCountry(lead.getCountry());
        dto.setCreationDate(lead.getCreationDate());
        dto.setCompanyName(lead.getCompanyName());
        dto.setState(lead.getState());
        return dto;
    }

    public static Lead toModel(InternalLeadDTO dto) {
        // Convertit un DTO Thrift en objet métier (sans id)
        Lead l = new Lead();
        l.setFirstName(dto.getFirstName());
        l.setLastName(dto.getLastName());
        l.setAnnualRevenue(dto.getAnnualRevenue());
        l.setPhone(dto.getPhone());
        l.setStreet(dto.getStreet());
        l.setPostalCode(dto.getPostalCode());
        l.setCity(dto.getCity());
        l.setCountry(dto.getCountry());
        l.setCreationDate(dto.getCreationDate());
        l.setCompanyName(dto.getCompanyName());
        l.setState(dto.getState());
        return l;
    }

    public static List<InternalLeadDTO> toDtoList(List<Lead> leads) {
        List<InternalLeadDTO> out = new ArrayList<>();
        if (leads == null) return out;
        for (Lead l : leads) out.add(toDto(l));
        return out;
    }
}
