package org.example.client;

import org.example.internal.InternalCRM;
import org.example.internal.InternalLeadDTO;
import org.example.dto.VirtualLeadDTO;
import org.example.util.LeadMapper;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.List;
import java.util.stream.Collectors;

public class InternalCRMClient {

    private final InternalCRM.Client client;
    private final TTransport transport;

    public InternalCRMClient(String host, int port) throws Exception {
        this.transport = new TSocket(host, port);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        this.client = new InternalCRM.Client(protocol);
    }

    public List<VirtualLeadDTO> findLeads(double minRevenue, double maxRevenue, String province) {
        try {
            List<InternalLeadDTO> internalLeads = client.findLeads(minRevenue, maxRevenue, province);
            return internalLeads.stream()
                    .map(LeadMapper::toVirtualLead)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<VirtualLeadDTO> findLeadsByDate(String fromIso, String toIso) {
        try {
            List<InternalLeadDTO> internalLeads = client.findLeadsByDate(fromIso, toIso);
            return internalLeads.stream()
                    .map(LeadMapper::toVirtualLead)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Fermer le transport Thrift quand le client n'est plus utilisé
    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }
}
