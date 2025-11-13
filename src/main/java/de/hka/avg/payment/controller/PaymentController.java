package de.hka.avg.payment.controller;

import de.hka.avg.payment.dto.PaymentRequest;
import de.hka.avg.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment Service", description = "REST API für Zahlungsverarbeitung")
public class PaymentController {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/process")
    @Operation(summary = "Verarbeitet eine Zahlung", 
               description = "Prüft Kontostand und bucht ab. Gibt true/false zurück.")
    public ResponseEntity<Boolean> processPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("📨 Payment request: {}", request.getOrderId());
        boolean success = paymentService.processPayment(request);
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health Check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is running! 🚀");
    }
}