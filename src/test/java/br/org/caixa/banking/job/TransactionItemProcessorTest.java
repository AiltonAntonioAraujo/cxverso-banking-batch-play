package br.org.caixa.banking.job;


import br.org.caixa.banking.job.transaction.TransactionItemProcessor;
import br.org.caixa.banking.model.*;
import br.org.caixa.banking.model.TransactionCsvRecord;
import br.org.caixa.banking.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionItemProcessor - Unit Tests")
class TransactionItemProcessorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionItemProcessor processor;

    private Account existingAccount;

    @BeforeEach
    void setUp() {
        existingAccount = Account.builder()
                .accountNumber("ACC-001")
                .ownerName("Alice Mendes")
                .email("alice@bank.com")
                .accountType("SAVINGS")
                .balance(new BigDecimal("5000.00"))
                .interestRate(new BigDecimal("0.0650"))
                .build();
    }

    @Test
    @DisplayName("Should process valid CREDIT transaction successfully")
    void shouldProcessValidCreditTransaction() throws Exception {
        when(accountRepository.findByAccountNumber("ACC-001"))
                .thenReturn(Optional.of(existingAccount));

        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setAccountNumber("ACC-001");
        record.setTransactionType("CREDIT");
        record.setAmount("500.00");
        record.setDescription("Salary");
        record.setTransactionDate("2024-01-05 09:00:00");

        Transaction result = processor.process(record);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("ACC-001");
        assertThat(result.getTransactionType()).isEqualTo("CREDIT");
        assertThat(result.getAmount()).isEqualByComparingTo("500.00");
        assertThat(result.getStatus()).isEqualTo("PROCESSED");
        assertThat(result.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return null for unknown account (skip item)")
    void shouldReturnNullForUnknownAccount() throws Exception {
        when(accountRepository.findByAccountNumber("ACC-999"))
                .thenReturn(Optional.empty());

        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setAccountNumber("ACC-999");
        record.setTransactionType("CREDIT");
        record.setAmount("100.00");
        record.setDescription("Test");
        record.setTransactionDate("2024-01-05 09:00:00");

        Transaction result = processor.process(record);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null for invalid transaction type")
    void shouldReturnNullForInvalidTransactionType() throws Exception {
        when(accountRepository.findByAccountNumber("ACC-001"))
                .thenReturn(Optional.of(existingAccount));

        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setAccountNumber("ACC-001");
        record.setTransactionType("TRANSFER");
        record.setAmount("100.00");
        record.setDescription("Test");
        record.setTransactionDate("2024-01-05 09:00:00");

        Transaction result = processor.process(record);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null for negative amount")
    void shouldReturnNullForNegativeAmount() throws Exception {
        when(accountRepository.findByAccountNumber("ACC-001"))
                .thenReturn(Optional.of(existingAccount));

        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setAccountNumber("ACC-001");
        record.setTransactionType("DEBIT");
        record.setAmount("-50.00");
        record.setDescription("Invalid");
        record.setTransactionDate("2024-01-05 09:00:00");

        Transaction result = processor.process(record);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null for non-numeric amount")
    void shouldReturnNullForNonNumericAmount() throws Exception {
        when(accountRepository.findByAccountNumber("ACC-001"))
                .thenReturn(Optional.of(existingAccount));

        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setAccountNumber("ACC-001");
        record.setTransactionType("CREDIT");
        record.setAmount("abc");
        record.setDescription("Invalid");
        record.setTransactionDate("2024-01-05 09:00:00");

        Transaction result = processor.process(record);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should process valid DEBIT transaction successfully")
    void shouldProcessValidDebitTransaction() throws Exception {
        when(accountRepository.findByAccountNumber("ACC-001"))
                .thenReturn(Optional.of(existingAccount));

        TransactionCsvRecord record = new TransactionCsvRecord();
        record.setAccountNumber("ACC-001");
        record.setTransactionType("DEBIT");
        record.setAmount("200.00");
        record.setDescription("ATM withdrawal");
        record.setTransactionDate("2024-01-06 10:30:00");

        Transaction result = processor.process(record);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionType()).isEqualTo("DEBIT");
        assertThat(result.getAmount()).isEqualByComparingTo("200.00");
    }
}

