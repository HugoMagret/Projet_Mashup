package org.calculator;

import org.apache.thrift.TProcessor;
import org.calculator.thrift.CalculatorService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class ThrifCalculatorServiceServlet extends org.calculator.ThriftHttpServletTemplate {


    public ThrifCalculatorServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static TProcessor createProcessor() {
        return new CalculatorService.Processor<CalculatorService.Iface>(new org.calculator.CalculatorServiceImpl());
    }

    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

}
