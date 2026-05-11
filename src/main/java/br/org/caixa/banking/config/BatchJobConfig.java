package br.org.caixa.banking.config;

import br.org.caixa.banking.job.transaction.TransactionItemProcessor;
import br.org.caixa.banking.job.transaction.TransactionItemReader;
import br.org.caixa.banking.job.transaction.TransactionWriter;
import br.org.caixa.banking.listener.BankingJobExecutionListener;
import br.org.caixa.banking.model.Transaction;
import br.org.caixa.banking.model.TransactionCsvRecord;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final BankingJobExecutionListener jobListener;

    // ─────────────────────────────────────────────────────────────────
    // JOB 1: Transaction Processing (CSV → DB)
    // Feature: FlatFileItemReader + ItemProcessor with null-filter (skip)
    // ─────────────────────────────────────────────────────────────────
    @Bean
    public Job transactionProcessingJoB(TransactionItemReader readerFactory, TransactionItemProcessor processor, TransactionWriter writer) {
        Step step = new StepBuilder("trasactionStep", jobRepository)
                .<TransactionCsvRecord, Transaction> chunk(50, transactionManager)
                .reader(transactionFileReader(readerFactory, null))
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();
        return new JobBuilder("transactionProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step)
                .build();

    }

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionCsvRecord> transactionFileReader(
            TransactionItemReader factory,
            @Value("#{jobParameters['input.file'] ?: null}") String filePath) {

        Resource resource = (filePath != null)
                ? new FileSystemResource(filePath)
                : new ClassPathResource("data/transactions.csv");

        return factory.reader(resource);
    }



}
