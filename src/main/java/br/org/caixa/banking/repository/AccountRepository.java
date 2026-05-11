package br.org.caixa.banking.repository;

import br.org.caixa.banking.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {


    public Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByAccountType(String accountType);

    @Query("SELECT a FROM Account a WHERE a.accountType = 'SAVINGS'")
    List<Account> findAllSavingsAccounts();
}
