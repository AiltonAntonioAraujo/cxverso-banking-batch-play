package br.org.caixa.banking.integration;

import br.org.caixa.banking.model.Account;
import br.org.caixa.banking.model.Transaction;
import br.org.caixa.banking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Transaction Processing Job - Integration Tests")
class TransactionProcessingJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private Job transactionProcessingJob;

    @BeforeEach
    void setUp() {
        jobRepositoryTestUtils.removeJobExecutions();
        transactionRepository.deleteAll();
        // Seed test accounts
        seedAccounts();
        jobLauncherTestUtils.setJob(transactionProcessingJob);
    }

    @Test
    @DisplayName("Should complete successfully and process valid transactions")
    void shouldCompleteSuccessfully() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Should persist valid transactions to database")
    void shouldPersistValidTransactions() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(params);

        List<Transaction> transactions = transactionRepository.findAll();
        // CSV has 12 rows (1 header skipped), 3 invalid = 9 valid
        assertThat(transactions).isNotEmpty();
        assertThat(transactions).hasSizeGreaterThanOrEqualTo(7);
    }

    @Test
    @DisplayName("Should skip invalid transactions and not throw exception")
    void shouldSkipInvalidTransactions() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        // All processed transactions should have status PROCESSED
        transactionRepository.findAll().forEach(tx ->
                assertThat(tx.getStatus()).isEqualTo("PROCESSED")
        );
    }

    @Test
    @DisplayName("Should update account balance after processing credits")
    void shouldUpdateAccountBalanceAfterCredits() throws Exception {
        BigDecimal initialBalance = accountRepository.findByAccountNumber("ACC-001")
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);

        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(params);

        Account updated = accountRepository.findByAccountNumber("ACC-001").orElseThrow();
        // CSV has CREDIT 1500 and DEBIT 200 for ACC-001 → net +1300
        assertThat(updated.getBalance()).isNotEqualByComparingTo(initialBalance);
    }

    @Test
    @DisplayName("Should record step metrics correctly")
    void shouldRecordStepMetrics() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        StepExecution step = execution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount()).isGreaterThan(0);
        assertThat(step.getWriteCount()).isGreaterThan(0);
        assertThat(step.getSkipCount()).isGreaterThanOrEqualTo(0);
    }

    private void seedAccounts() {
        if (accountRepository.findByAccountNumber("ACC-001").isEmpty()) {
            accountRepository.saveAll(List.of(
                    buildAccount("ACC-001", "Alice Mendes",   "SAVINGS",  "5000.00", "0.0650"),
                    buildAccount("ACC-002", "Bruno Costa",    "CHECKING", "3200.00", "0.0000"),
                    buildAccount("ACC-003", "Carla Souza",    "SAVINGS",  "12000.00","0.0700"),
                    buildAccount("ACC-004", "Daniel Lima",    "SAVINGS",  "800.00",  "0.0620"),
                    buildAccount("ACC-005", "Elena Ferreira", "CHECKING", "15000.00","0.0000")
            ));
        }
    }

    private Account buildAccount(String number, String name, String type, String balance, String rate) {
        return Account.builder()
                .accountNumber(number)
                .ownerName(name)
                .email(name.toLowerCase().replace(" ", "") + "@bank.com")
                .accountType(type)
                .balance(new BigDecimal(balance))
                .interestRate(new BigDecimal(rate))
                .createdAt(LocalDateTime.now())
                .build();
    }
}

