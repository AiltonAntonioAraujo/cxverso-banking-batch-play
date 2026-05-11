package br.org.caixa.banking.job.transaction;

import br.org.caixa.banking.model.Account;
import br.org.caixa.banking.model.Transaction;
import br.org.caixa.banking.repository.AccountRepository;
import br.org.caixa.banking.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends Transaction> chunk) throws Exception {
        for(Transaction transaction : chunk.getItems()){
            transactionRepository.save(transaction);

            Optional<Account> accountOpt = accountRepository.findByAccountNumber(transaction.getAccountNumber());
            accountOpt.ifPresent(account -> {
                if("CREDIT".equals(transaction.getTransactionType())){
                    account.setBalance(account.getBalance().add(transaction.getAmount()));
                } else{
                    BigDecimal newBalance = account.getBalance().subtract(transaction.getAmount());
                    account.setBalance(newBalance.max(BigDecimal.ZERO));
                }
                accountRepository.save(account);
                log.debug("Updated balance for account {}: {}", account.getAccountNumber(), account.getBalance());

            });

        }
        log.info("Written {} transactions to database", chunk.size());
    }
}
