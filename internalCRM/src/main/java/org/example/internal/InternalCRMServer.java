package org.example.internal;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * SERVEUR CRM INTERNE — Serveur Thrift simple
 *
 * But : démarrer un serveur Thrift (socket TCP) pour exposer l'interface InternalCRM.
 * Points importants :
 * - port par défaut : 9090 (modifiable via -Pport)
 * - ce serveur est de type simple (TSimpleServer) : utile pour tests locaux,
 *   pas pour la mise en production (pas de montée en charge).
 *
 * Exemples :
 *  ./gradlew :internalCRM:runInternalCRMServer
 *  ./gradlew :internalCRM:runInternalCRMServer -Pport=8080
 */
public class InternalCRMServer {

    // Port par défaut (choix arbitraire) – pourrait être externalisé (env / args)
    public static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) throws Exception {
        // Récupérer le port depuis les arguments ou utiliser 9090 par défaut
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { 
                port = Integer.parseInt(args[0]); 
            } catch (NumberFormatException ignore) { 
                // Si le port n'est pas un nombre valide, on garde le port par défaut
            }
        }

        // Créer le gestionnaire qui répond aux requêtes (notre code métier)
        InternalCRMHandler handler = new InternalCRMHandler();
        InternalCRM.Processor<InternalCRMHandler> processor = new InternalCRM.Processor<>(handler);

        // Créer la "prise" réseau pour écouter les connexions
        TServerTransport serverTransport = new TServerSocket(port);

        // Créer le serveur (version simple qui traite une requête à la fois)
        TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

        System.out.println("[InternalCRM] Serveur démarré sur le port " + port + ". Ctrl+C pour arrêter.");
        server.serve(); // Boucle infinie qui attend les clients
    }
}
