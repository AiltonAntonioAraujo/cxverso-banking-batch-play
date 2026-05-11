package br.org.caixa.banking.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private String transactionType; // CREDIT, DEBIT
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;
    private String description;
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    private String status; // PENDING, PROCESSED, FAILED
    private String errorMessage;
    private LocalDateTime processedAt;

}
