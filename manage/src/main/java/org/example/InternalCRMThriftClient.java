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
        // Configurer un timeout de 60 secondes pour la connexion ET les opérations de lecture/écriture
        // Note: ce timeout s'applique à toutes les opérations sur le socket
        socket.setTimeout(60000);
        this.transport = socket;
        try {
            this.transport.open();
            System.out.println("[InternalCRMThriftClient] Connexion établie avec succès");
        } catch (Exception e) {
            System.err.println("[InternalCRMThriftClient] ERREUR : Impossible de se connecter au serveur InternalCRM");
            System.err.println("  Vérifiez que le serveur est démarré");
            System.err.println("  Ou démarrez uniquement InternalCRM : ./gradlew :internalCRM:runInternalCRMServer");
            System.err.println("  NOTE : Le serveur InternalCRM utilise TSimpleServer qui traite une requête à la fois.");
            System.err.println("  Si VirtualCRM ou une autre application utilise le serveur, vos requêtes seront bloquées.");
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
        System.out.println("[InternalCRMThriftClient] Création d'un lead : " + lead.getFirstName() + " " + lead.getLastName());
        
        // Test de connexion rapide avant de créer le lead
        try {
            System.out.println("[InternalCRMThriftClient] Test de connexion...");
            client.findLeads(0.0, 1.0, null); // Test rapide pour vérifier que le serveur répond
            System.out.println("[InternalCRMThriftClient] Serveur répond, création du lead...");
        } catch (Exception e) {
            System.err.println("[InternalCRMThriftClient] ERREUR : Le serveur ne répond pas au test de connexion");
            System.err.println("  Vérifiez que le serveur InternalCRM est bien démarré et accessible");
            throw new org.apache.thrift.TException("Serveur InternalCRM non accessible : " + e.getMessage(), e);
        }
        
        long startTime = System.currentTimeMillis();
        try {
            long id = client.createLead(lead);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[InternalCRMThriftClient] Lead créé avec succès (ID: " + id + ") en " + duration + " ms");
            return id;
        } catch (org.apache.thrift.TException e) {
            long duration = System.currentTimeMillis() - startTime;
            System.err.println("[InternalCRMThriftClient] Erreur après " + duration + " ms : " + e.getMessage());
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                System.err.println("[InternalCRMThriftClient] ⚠️ TIMEOUT : Le serveur InternalCRM utilise TSimpleServer qui traite UNE requête à la fois.");
            }
            throw e;
        }
    }

    public void deleteLead(InternalLeadDTO lead) throws ThriftNoSuchLeadException, org.apache.thrift.TException {
        System.out.println("[InternalCRMThriftClient] Suppression d'un lead : " + lead.getFirstName() + " " + lead.getLastName());
        long startTime = System.currentTimeMillis();
        try {
            client.deleteLead(lead);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[InternalCRMThriftClient] Lead supprimé avec succès en " + duration + " ms");
        } catch (org.apache.thrift.TException e) {
            long duration = System.currentTimeMillis() - startTime;
            System.err.println("[InternalCRMThriftClient] Erreur après " + duration + " ms : " + e.getMessage());
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                System.err.println("[InternalCRMThriftClient] ⚠️ TIMEOUT : Le serveur InternalCRM utilise TSimpleServer qui traite UNE requête à la fois.");
            }
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }
}

