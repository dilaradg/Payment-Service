package de.hka.avg.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private String paymentId;
    private String orderId;
    private String status;  // SUCCESS, FAILED, PENDING
    private Double amount;
    private String currency;
    private String transactionId;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}