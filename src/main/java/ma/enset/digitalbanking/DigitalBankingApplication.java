package ma.enset.digitalbanking;

import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.AccountStatus;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceAccountInsufficient;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.repositories.AccountOperationRepository;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApplication.class, args);
    }


    @Bean
    CommandLineRunner commmadLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Soufiane", "Abdelkrim", "Mohammed", "Hassan", "Yassine", "Ahmed").forEach(name -> {
                Customer customer = new Customer(null, name, name + "@gmail.com", null);
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                    List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
                    for (BankAccount account : bankAccounts) {
                        for (int i = 0; i < 10; i++) {
                            bankAccountService.credit(account.getId(), 1000 + Math.random() * 12000, "Credit");
                            bankAccountService.debit(account.getId(), 1000 + Math.random() * 9000, "Debit");
                        }
                    }
                } catch (CustomerNotFoundException | BankAccountNotFoundException | BalanceAccountInsufficient e) {
                    e.printStackTrace();
                }
            });
        };
    }

//    @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepsitory) {
        return args -> {
            Stream.of("Soufiane", "Abdelkrim", "Mohammed", "Hassan", "Yassine", "Ahmed").forEach(name -> {
                Customer customer = new Customer(null, name, name+"@gmail.com", null);
                customerRepository.save(customer);
            });

            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5);
                bankAccountRepository.save(savingAccount);

                bankAccountRepository.findAll().forEach(bankAccount -> {
                    for (int i = 0; i < 10; i++) {
                        AccountOperation accountOperation = new AccountOperation();
                        accountOperation.setOperationDate(new Date());
                        accountOperation.setAmount(Math.random()*1200);
                        accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                        accountOperation.setBankAccount(bankAccount);
                        accountOperationRepsitory.save(accountOperation);
                    }
                });
            });

        };
    }
}
