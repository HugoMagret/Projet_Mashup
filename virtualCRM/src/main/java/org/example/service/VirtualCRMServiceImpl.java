package org.example.service;


import org.example.client.GeoClient;
import org.example.client.InternalCRMClient;
import org.example.client.SalesforceClient;
import org.example.dto.VirtualLeadDTO;
import org.example.util.LeadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VirtualCRMServiceImpl implements VirtualCRMService {

    private final InternalCRMClient internalCRMClient;
    private final SalesforceClient salesforceClient;
    private final GeoClient geoClient;

    /*
    @Autowired
    public VirtualCRMServiceImpl(
            InternalCRMClient internalCRMClient,
            SalesforceClient salesforceClient,
            GeoClient geoClient) {
        this.internalCRMClient = internalCRMClient;
        this.salesforceClient = salesforceClient;
        this.geoClient = geoClient;
    }*/
    @Autowired
    public VirtualCRMServiceImpl(InternalCRMClient internalCRMClient) {
        this.internalCRMClient = internalCRMClient;
        this.salesforceClient = null;
        this.geoClient = null;
    }





    @Override
    public List<VirtualLeadDTO> findLeads(double minRevenue, double maxRevenue, String province) {


        // Get leads "InternalCRM"
        List<VirtualLeadDTO> internalLeads = internalCRMClient.findLeads(minRevenue, maxRevenue, province);

        // Get leads "Salesforce"
        /*
        List<VirtualLeadDTO> salesforceLeads = salesforceClient.findLeads(minRevenue, maxRevenue, province)
                .stream()
                .map(LeadMapper::toVirtualLead)
                .collect(Collectors.toList());
        */

        List<VirtualLeadDTO> allLeads = new ArrayList<>();
        allLeads.addAll(internalLeads);
        //allLeads.addAll(salesforceLeads);
        /*
        allLeads.forEach(lead -> {
            try {
                lead.setGeographicPoint(geoClient.getCoordinates(lead.getFullAddress()));
            } catch (Exception e) {
                lead.setGeographicPoint(null);
            }
        });
        */
        allLeads.sort(Comparator.comparingDouble(VirtualLeadDTO::getAnnualRevenue).reversed());

        return allLeads;
    }

    // TEMP
    @Override
    public List<VirtualLeadDTO> findLeadsByDate(String startDate, String endDate) {
        return List.of();
    }
}
