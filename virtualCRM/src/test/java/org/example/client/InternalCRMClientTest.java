package org.example.client;

import org.example.dto.VirtualLeadDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InternalCRMClientTest {

    @Test
    public void testFindLeads() throws Exception {
        // Connexion au serveur Thrift sur le port 9090
        InternalCRMClient client = new InternalCRMClient("localhost", 9090);

        List<VirtualLeadDTO> leads = client.findLeads(10000, 150000, "Maine-et-Loire");

        assertNotNull(leads, "La liste des leads ne doit pas être nulle");
        assertFalse(leads.isEmpty(), "La liste des leads ne doit pas être vide");

        VirtualLeadDTO lead = leads.get(0);
        System.out.println("Lead récupéré : " + lead.getFirstName() + " / " + lead.getLastName());
        assertEquals("Dupont", lead.getLastName());
        assertEquals("Jean", lead.getFirstName());

        client.close();
    }
}
