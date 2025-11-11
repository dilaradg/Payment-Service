package de.hka.avg.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("\n" +
                "==============================================\n" +
                "  Payment Service läuft auf Port 8082\n" +
                "  Swagger UI: http://localhost:8082/swagger-ui.html\n" +
                "==============================================\n");
    }
}