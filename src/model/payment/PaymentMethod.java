package model.payment;

public interface PaymentMethod {
    String getMethodName();
    boolean processPayment(double amount);
}
