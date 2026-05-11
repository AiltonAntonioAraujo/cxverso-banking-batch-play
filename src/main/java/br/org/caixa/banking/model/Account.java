package br.org.caixa.banking.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String ownerName;
    private String email;
    private String accountType; // SAVINGS, CHECKING
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate; // e.g. 0.0650 = 6.5% per year
    private LocalDate lastStatementDate;
    private LocalDateTime lastInterestCalculation;
    private LocalDateTime createdAt;

}
