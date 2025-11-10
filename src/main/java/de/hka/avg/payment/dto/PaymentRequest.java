package de.hka.avg.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotBlank(message = "OrderId darf nicht leer sein")
    private String orderId;
    
    @NotBlank(message = "CustomerId darf nicht leer sein")
    private String customerId;
    
    @NotNull(message = "Amount darf nicht null sein")
    @Positive(message = "Amount muss positiv sein")
    private Double amount;
    
    @NotBlank(message = "Currency darf nicht leer sein")
    private String currency;
    
    @NotBlank(message = "PaymentMethod darf nicht leer sein")
    private String paymentMethod;
    
    private String customerName;
}