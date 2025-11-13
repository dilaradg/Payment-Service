package de.hka.avg.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequest {
    
    @NotBlank(message = "Order ID darf nicht leer sein")
    private String orderId;
    
    @NotBlank(message = "Kundenname darf nicht leer sein")
    private String customerName;
    
    @NotNull(message = "Betrag darf nicht null sein")
    @DecimalMin(value = "0.01", message = "Betrag muss größer als 0 sein")
    private BigDecimal amount;
    
    // Konstruktoren
    public PaymentRequest() {
    }
    
    public PaymentRequest(String orderId, String customerName, BigDecimal amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
    }
    
    // Getter & Setter
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}