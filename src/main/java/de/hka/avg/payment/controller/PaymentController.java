package de.hka.avg.payment.controller;

import de.hka.avg.payment.dto.ErrorResponse;
import de.hka.avg.payment.dto.PaymentRequest;
import de.hka.avg.payment.dto.PaymentResponse;
import de.hka.avg.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Service", description = "REST API für Zahlungsverarbeitung")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/process")
    @Operation(summary = "Verarbeitet eine Zahlung", 
               description = "Nimmt eine Zahlungsanforderung entgegen und verarbeitet diese")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Zahlung erfolgreich verarbeitet",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Ungültige Anfrage",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "402", description = "Zahlung fehlgeschlagen",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    })
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("📨 Received payment request for order: {}", request.getOrderId());
        
        try {
            // SPEZIAL LOGIK für Testzwecke
            PaymentResponse response;
            
            if (request.getOrderId().contains("TEST-SUCCESS")) {
                response = paymentService.processPaymentForceSuccess(request);
            } else if (request.getOrderId().contains("TEST-FAIL")) {
                response = paymentService.processPaymentForceFailure(request);
            } else {
                response = paymentService.processPayment(request);
            }
            
            // Je nach Status anderen HTTP Code zurückgeben
            if ("SUCCESS".equals(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
            }
            
        } catch (Exception e) {
            log.error("Error processing payment: ", e);
            ErrorResponse error = ErrorResponse.builder()
                    .status("ERROR")
                    .errorCode("INTERNAL_ERROR")
                    .message("Internal server error: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/{paymentId}")
    @Operation(summary = "Ruft Zahlungsstatus ab",
               description = "Gibt den Status einer Zahlung zurück")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Zahlungsstatus erfolgreich abgerufen",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Zahlung nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentId) {
        log.info("📨 Received request for payment status: {}", paymentId);
        
        PaymentResponse response = paymentService.getPaymentStatus(paymentId);
        
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse error = ErrorResponse.builder()
                    .status("NOT_FOUND")
                    .errorCode("PAYMENT_NOT_FOUND")
                    .message("Payment with ID " + paymentId + " not found")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Prüft ob der Service läuft")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is running! 🚀");
    }
}