package br.edu.ppi.seller.exception;

public class OrderSendResponseException extends RuntimeException {
    public OrderSendResponseException(String message) {
        super(message);
    }
}
