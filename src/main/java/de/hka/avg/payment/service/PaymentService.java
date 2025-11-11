package de.hka.avg.payment.service;

import de.hka.avg.payment.dto.PaymentRequest;
import de.hka.avg.payment.dto.PaymentResponse;
import de.hka.avg.payment.model.Customer;
import de.hka.avg.payment.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final CustomerRepository customerRepository;
    
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("💳 Payment für: {} | Betrag: {}", 
                 request.getCustomerName(), request.getAmount());
        
        // Kunde suchen
        Customer customer = customerRepository.findByName(request.getCustomerName());
        
        if (customer == null) {
            log.warn("❌ Kunde nicht gefunden");
            return PaymentResponse.builder()
                    .success(false)
                    .message("Kunde nicht gefunden")
                    .build();
        }
        
        // Geld checken
        if (!customer.hasEnoughMoney(request.getAmount())) {
            log.warn("❌ Nicht genug Geld: {} < {}", 
                     customer.getBalance(), request.getAmount());
            return PaymentResponse.builder()
                    .success(false)
                    .message("Nicht genug Geld")
                    .build();
        }
        
        // Geld abbuchen
        customer.takeMoney(request.getAmount());
        customerRepository.save(customer);
        
        log.info("✅ Zahlung OK! Neuer Kontostand: {}", customer.getBalance());
        
        return PaymentResponse.builder()
                .success(true)
                .message("Zahlung erfolgreich!")
                .build();
    }
}