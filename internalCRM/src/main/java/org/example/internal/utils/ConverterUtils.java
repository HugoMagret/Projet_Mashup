package org.example.internal.utils;

import org.example.internal.InternalLeadDTO;
import org.example.internal.model.Lead;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Utilitaire de conversion entre le modèle métier `Lead` et le DTO Thrift `InternalLeadDTO`.
 * Les méthodes sont simples et copies les champs un par un.
 */
public final class ConverterUtils {
    /**
     * Utilitaires de conversion entre le modèle Java interne (Lead)
     * et les DTO générés par Thrift (InternalLeadDTO).
     * Les méthodes font une copie champ-à-champ simple.
     */
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
        dto.setCreationDate(calendarToIsoString(lead.getCreationDate()));
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
        l.setCreationDate(isoStringToCalendar(dto.getCreationDate()));
        l.setCompanyName(dto.getCompanyName());
        l.setState(dto.getState());
        return l;
    }

    // Convertit une liste de Lead en liste de DTO

    public static List<InternalLeadDTO> toDtoList(List<Lead> leads) {
        List<InternalLeadDTO> out = new ArrayList<>();
        if (leads == null) return out;
        for (Lead l : leads) out.add(toDto(l));
        return out;
    }

    // ----- Conversion ISO-8601 <-> Calendar
    private static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Convertit une chaine ISO-8601 en Calendar Java.
     * Retourne null si le format est invalide.
     */
    public static Calendar isoStringToCalendar(String iso) {
        if (iso == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date d = sdf.parse(iso);
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.setTime(d);
            return c;
        } catch (ParseException e) {
            // En cas d'échec de parsing, retourner null — le modèle doit valider
            return null;
        }
    }

    /**
     * Convertit un Calendar Java en chaine ISO-8601.
     * Format de sortie : "yyyy-MM-dd'T'HH:mm:ss'Z'" (UTC)
     */
    public static String calendarToIsoString(Calendar c) {
        if (c == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(c.getTime());
    }
}
