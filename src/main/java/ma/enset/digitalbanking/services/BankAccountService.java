package ma.enset.digitalbanking.services;

import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CurrentAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.SavingAccountDTO;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.exceptions.BalanceAccountInsufficient;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customer);
    CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceAccountInsufficient;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BalanceAccountInsufficient, BankAccountNotFoundException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);
}
