package org.example.service;

import org.example.dto.VirtualLeadDTO;

import java.util.List;


public interface VirtualCRMService {

    List<VirtualLeadDTO> findLeads(double minRevenue, double maxRevenue, String province);
    List<VirtualLeadDTO> findLeadsByDate(String startDate, String endDate);
}