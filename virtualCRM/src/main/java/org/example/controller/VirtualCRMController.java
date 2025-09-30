package org.example.controller;

import org.example.dto.VirtualLeadDTO;
import org.example.service.VirtualCRMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class VirtualCRMController {

    private final VirtualCRMService virtualCRMService;

    @Autowired
    public VirtualCRMController(VirtualCRMService virtualCRMService) {
        this.virtualCRMService = virtualCRMService;
    }

    @GetMapping
    public List<VirtualLeadDTO> findLeads(
            @RequestParam double minRevenue,
            @RequestParam double maxRevenue,
            @RequestParam String province) {
        return virtualCRMService.findLeads(minRevenue, maxRevenue, province);
    }

    @GetMapping("/byDate")
    public List<VirtualLeadDTO> findLeadsByDate(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return virtualCRMService.findLeadsByDate(startDate, endDate);
    }
}
