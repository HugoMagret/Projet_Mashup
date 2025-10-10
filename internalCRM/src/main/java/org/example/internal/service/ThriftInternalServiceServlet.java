package org.example.internal.service;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.apache.thrift.transport.TTransportException;
import org.example.internal.InternalCRM;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet Thrift pour InternalCRM (exposée sous /thrift/internalcrm).
 * Elle utilise directement TServlet. Une alternative est d'utiliser
 * `ThriftHttpServletTemplate` pour une intégration plus contrôlée.
 */
@WebServlet(name = "InternalCRMThrift", urlPatterns = {"/thrift/internalcrm"})
public class ThriftInternalServiceServlet extends TServlet {

    public ThriftInternalServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static InternalCRM.Processor<InternalServiceImpl> createProcessor() {
        return new InternalCRM.Processor<>(new InternalServiceImpl());
    }

    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }
}
