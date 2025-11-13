package de.hka.avg.payment.service;

import de.hka.avg.payment.dto.PaymentRequest;
import de.hka.avg.payment.messaging.MessageSender;
import de.hka.avg.payment.model.Customer;
import de.hka.avg.payment.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final CustomerRepository customerRepository;
    private final MessageSender messageSender;
    
    public PaymentService(CustomerRepository customerRepository, MessageSender messageSender) {
        this.customerRepository = customerRepository;
        this.messageSender = messageSender;
    }
    
    public boolean kontostandPruefen(String name, BigDecimal preis) {
        Customer kunde = customerRepository.getAllCustomers()
                .values()
                .stream()
                .filter(k -> name.equals(k.getName()))
                .findFirst()
                .orElse(null);
        
        if (kunde == null) {
            return false;
        }
        
        if (kunde.getBalance().compareTo(preis) < 0) {
            return false;
        }
        
        return true;
    }
    
    public boolean processPayment(PaymentRequest request) {
        log.info("Payment für: {} | Betrag: {}", 
                 request.getCustomerName(), request.getAmount());
        
        if (!kontostandPruefen(request.getCustomerName(), request.getAmount())) {
            log.warn("Zahlung fehlgeschlagen");
            
            // Message an Queue: Payment Failed
            messageSender.sendToQueue(
                "payment_queue", 
                "payment_failed", 
                "Order " + request.getOrderId() + " - Insufficient funds"
            );
            
            return false;
        }
        
        Customer customer = customerRepository.findByName(request.getCustomerName());
        customer.takeMoney(request.getAmount());
        customerRepository.save(customer);
        
        log.info("Zahlung OK! Neuer Kontostand: {}", customer.getBalance());
        
        // Message an Queue: Payment Success
        messageSender.sendToQueue(
            "payment_queue", 
            "payment_success", 
            "Order " + request.getOrderId() + " - Payment successful: " + request.getAmount() + " EUR"
        );
        
        return true;
    }
}
