package org.example;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.example.internal.InternalCRM;
import org.example.internal.InternalLeadDTO;
import org.example.internal.ThriftNoSuchLeadException;
import org.example.internal.ThriftWrongOrderForRevenueException;
import org.example.internal.ThriftWrongStateException;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Petit wrapper autour du client Thrift InternalCRM.
 * Permet :
 *  - de se connecter/déconnecter proprement
 *  - de récupérer tous les leads
 *  - de créer/supprimer des leads
 */
public class InternalCRMThriftClient implements Closeable {

    private final TTransport transport;
    private final InternalCRM.Client client;

    public InternalCRMThriftClient(String host, int port) throws Exception {
        System.out.println("[InternalCRMThriftClient] Connexion au serveur InternalCRM sur " + host + ":" + port + "...");
        TSocket socket = new TSocket(host, port);
        // Configurer un timeout de 10 secondes pour éviter les blocages infinis
        socket.setTimeout(10000);
        this.transport = socket;
        try {
            this.transport.open();
            System.out.println("[InternalCRMThriftClient] Connexion établie avec succès");
        } catch (Exception e) {
            System.err.println("[InternalCRMThriftClient] ERREUR : Impossible de se connecter au serveur InternalCRM");
            System.err.println("  Vérifiez que le serveur est démarré : ./gradlew startAllServers");
            System.err.println("  Ou démarrez uniquement InternalCRM : ./gradlew :internalCRM:runInternalCRMServer");
            throw new Exception("Connexion échouée au serveur InternalCRM sur " + host + ":" + port + ". " + e.getMessage(), e);
        }
        TProtocol protocol = new TBinaryProtocol(transport);
        this.client = new InternalCRM.Client(protocol);
    }

    public InternalCRM.Client getClient() {
        return client;
    }

    /**
     * Récupère "tous" les leads internes : on utilise l'astuce
     * findLeads(0, Double.MAX_VALUE, null) comme tu l'as déjà fait
     * dans VerificationDataLoader.
     */
    public List<InternalLeadDTO> findAllLeads() throws ThriftWrongOrderForRevenueException, ThriftWrongStateException, org.apache.thrift.TException {
        return client.findLeads(0.0, Double.MAX_VALUE, null);
    }

    public long createLead(InternalLeadDTO lead) throws org.example.internal.ThriftWrongStateException, org.apache.thrift.TException {
        return client.createLead(lead);
    }

    public void deleteLead(InternalLeadDTO lead) throws ThriftNoSuchLeadException, org.apache.thrift.TException {
        client.deleteLead(lead);
    }

    @Override
    public void close() throws IOException {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }
}

