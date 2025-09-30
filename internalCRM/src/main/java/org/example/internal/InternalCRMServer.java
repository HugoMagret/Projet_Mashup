package org.example.internal;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Serveur Thrift très simple pour exposer InternalCRM (iteration 2.2).
 * Objectif pédagogique : montrer comment lancer le service interne.
 * Production : on utiliserait plutôt TThreadPoolServer ou un serveur non bloquant.
 */
public class InternalCRMServer {

    // Port par défaut (choix arbitraire) – pourrait être externalisé (env / args)
    public static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignore) { }
        }

        // Handler (implémentation métier en mémoire)
        InternalCRMHandler handler = new InternalCRMHandler();
        InternalCRM.Processor<InternalCRMHandler> processor = new InternalCRM.Processor<>(handler);

        // Transport bloquant simple
        TServerTransport serverTransport = new TServerSocket(port);

        // Serveur mono-thread (suffisant pour démo)
        TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

        System.out.println("[InternalCRM] Serveur démarré sur le port " + port + ". Ctrl+C pour arrêter.");
        server.serve();
    }
}
