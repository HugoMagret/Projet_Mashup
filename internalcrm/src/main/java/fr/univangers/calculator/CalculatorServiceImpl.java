package org.calculator;
import org.apache.thrift.TException;
import org.calculator.thrift.CalculatorService;
import org.calculator.thrift.CalculatorService.Iface;
import org.calculator.thrift.ThriftDivisionBy0;

public class CalculatorServiceImpl implements CalculatorService.Iface{
    @Override
    public int addition(int a, int b) throws TException {
        return a+b;
    }

    @Override
    public int subtraction(int a, int b) throws TException {
        return a-b;
    }

    @Override
    public int multiplication(int a, int b) throws TException {
        return a * b;
    }

    @Override
    public int division(int a, int b) throws ThriftDivisionBy0, TException {
        if (b==0)
            throw new ThriftDivisionBy0("division by 0");

        return a/b;
    }
}
