package ma.enset.digitalbanking.services;

import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.entities.CurrentAccount;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.entities.SavingAccount;
import ma.enset.digitalbanking.exceptions.BalanceAccountInsufficient;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customer);
    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceAccountInsufficient;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BalanceAccountInsufficient, BankAccountNotFoundException;

    List<BankAccount> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);
}
