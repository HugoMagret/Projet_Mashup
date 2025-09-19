
namespace java org.calculator.thrift


typedef i32 int

exception ThriftDivisionBy0 {
    1: string message
}


service CalculatorService
{
  int addition(1:int a, 2:int b),
  int subtraction(1:int a, 2:int b),
  int multiplication(1:int a, 2:int b),
  int division(1:int a, 2:int b) throws (1: ThriftDivisionBy0 e)
}