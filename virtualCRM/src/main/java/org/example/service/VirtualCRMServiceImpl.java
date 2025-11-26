package org.example.service;

import org.example.client.GeoClient;
import org.example.client.InternalCRMClient;
import org.example.client.SalesforceClient;
import org.example.dto.GeographicPointDTO;
import org.example.dto.VirtualLeadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VirtualCRMServiceImpl implements VirtualCRMService {

    private final InternalCRMClient internalCRMClient;
    private final SalesforceClient salesforceClient;
    private final GeoClient geoClient;


    @Autowired
    public VirtualCRMServiceImpl(InternalCRMClient internalCRMClient, SalesforceClient salesforceClient, GeoClient geoClient) {
        this.internalCRMClient = internalCRMClient;
        this.salesforceClient = salesforceClient;
        this.geoClient = geoClient;
    }



    @Override
    public List<VirtualLeadDTO> findLeads(double minRevenue, double maxRevenue, String province) {

        // Get leads "InternalCRM"
        List<VirtualLeadDTO> allLeads = internalCRMClient.findLeads(minRevenue, maxRevenue, province);
        // Get leads "Salesforce"
        allLeads.addAll(salesforceClient.findLeads(minRevenue, maxRevenue, province));

        // Applique la convertion de l'adresse réel en coordonées.
        // A savoir que le serveur public Nominatim ne permet pas de faire plus d'une requête
        // par seconde et seulement sur un seul thread, d'où l'attente très longue.
        // Lien de la usage policy : https://operations.osmfoundation.org/policies/nominatim/
        enrichWithGeoAndSort(allLeads);
        allLeads.sort(Comparator.comparing(VirtualLeadDTO::getAnnualRevenue).reversed());
        return allLeads;
    }


    @Override
    public List<VirtualLeadDTO> findLeadsByDate(String startDate, String endDate) {

        OffsetDateTime from = OffsetDateTime.parse(startDate);
        OffsetDateTime to = OffsetDateTime.parse(endDate);

        // get leads from internalCRM
        List<VirtualLeadDTO> allLeads = internalCRMClient.findLeadsByDate(startDate, endDate);
        // get leads from salesforce
        allLeads.addAll(salesforceClient.findLeadsByDate(startDate, endDate));


        // Même remarque que pour la méthode "findLeads(...)"
        enrichWithGeoAndSort(allLeads);
        return allLeads.stream()
                .filter(lead -> {
                    OffsetDateTime created = OffsetDateTime.parse(lead.getCreationDate());
                    return (created.isEqual(from) || created.isAfter(from))
                            && (created.isEqual(to) || created.isBefore(to));
                })
                // sort (newest to oldest)
                .sorted(Comparator.comparing(VirtualLeadDTO::getCreationDate).reversed())
                .collect(Collectors.toList());
    }

    private void enrichWithGeoAndSort(List<VirtualLeadDTO> leads) {
        for (VirtualLeadDTO lead : leads) {

            String address = lead.getFullAdress();
            // une adresse null ne devrais pas poser de problèmes
            GeographicPointDTO point = geoClient.geocode(address);
            lead.setGeographicPoint(point);
        }

        leads.sort(Comparator.comparing(VirtualLeadDTO::getAnnualRevenue).reversed());
    }


}
