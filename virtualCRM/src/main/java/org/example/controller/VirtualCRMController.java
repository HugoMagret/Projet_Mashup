package org.example.controller;

import org.example.dto.VirtualLeadDTO;
import org.example.service.VirtualCRMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
public class VirtualCRMController {

    private final VirtualCRMService virtualCRMService;

    @Autowired
    public VirtualCRMController(VirtualCRMService virtualCRMService) {
        this.virtualCRMService = virtualCRMService;
    }

    @GetMapping("/api/leads")
    public List<VirtualLeadDTO> findLeads(
            @RequestParam double minRevenue,
            @RequestParam double maxRevenue,
            @RequestParam String province) {
        return virtualCRMService.findLeads(minRevenue, maxRevenue, province);
    }

    @PostMapping("/virtualcrm/findLeads")
    public List<VirtualLeadDTO> findLeadsPost(@RequestBody Map<String, Object> criteria) {
        double minRevenue = criteria.containsKey("revenueMin") ? ((Number) criteria.get("revenueMin")).doubleValue()
                : 0;
        double maxRevenue = criteria.containsKey("revenueMax") ? ((Number) criteria.get("revenueMax")).doubleValue()
                : Double.MAX_VALUE;
        String province = (String) criteria.getOrDefault("state", "");

        return virtualCRMService.findLeads(minRevenue, maxRevenue, province);
    }

    @GetMapping("/api/leads/byDate")
    public List<VirtualLeadDTO> findLeadsByDate(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return virtualCRMService.findLeadsByDate(startDate, endDate);
    }
}
