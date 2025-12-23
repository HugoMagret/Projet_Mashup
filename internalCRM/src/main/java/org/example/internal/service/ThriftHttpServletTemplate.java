package org.example.internal.service;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.apache.thrift.TProcessor;

import javax.servlet.http.HttpServlet;
import java.util.Collection;
import java.util.Map;

/**
 * Template utilitaire pour exposer un service Thrift via une servlet HTTP.
 * Usage : étendre cette classe ou l'utiliser comme base pour construire la servlet.
 */
public class ThriftHttpServletTemplate extends HttpServlet {
    private final TServlet thriftServlet;

    public ThriftHttpServletTemplate(TProcessor processor, TProtocolFactory protocolFactory) {
        this.thriftServlet = new TServlet(processor, protocolFactory);
    }

    @Override
    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
            throws javax.servlet.ServletException, java.io.IOException {
        // Délégué à la servlet Thrift
        thriftServlet.service(req, resp);
    }

    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
            throws javax.servlet.ServletException, java.io.IOException {
        doPost(req, resp);
    }
}
