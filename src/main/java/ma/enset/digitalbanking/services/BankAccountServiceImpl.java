package ma.enset.digitalbanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.*;
import ma.enset.digitalbanking.entities.*;
import ma.enset.digitalbanking.enums.OperationType;
import ma.enset.digitalbanking.exceptions.BalanceAccountInsufficient;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.mappers.BankAccountMapperImpl;
import ma.enset.digitalbanking.repositories.AccountOperationRepository;
import ma.enset.digitalbanking.repositories.BankAccountRepository;
import ma.enset.digitalbanking.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Save a new Customer.");
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        return bankAccountMapper.fromCurrentAccount(bankAccountRepository.save(currentAccount));
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double intersetRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setInterestRate(intersetRate);
        savingAccount.setCustomer(customer);
        return bankAccountMapper.fromSavingAccount(bankAccountRepository.save(savingAccount));
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        /*
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerDTO customerDTO = bankAccountMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }
        */

        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> bankAccountMapper.fromCustomer(customer))
                .toList();

        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account not found");
        }
        if(bankAccount instanceof SavingAccount) {
            return bankAccountMapper.fromSavingAccount((SavingAccount) bankAccount);
        } else {
            return bankAccountMapper.fromCurrentAccount((CurrentAccount) bankAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceAccountInsufficient {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account not found");
        }
        if(bankAccount.getBalance() < amount) {
            throw new BalanceAccountInsufficient("Insufficient balance");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account not found");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BalanceAccountInsufficient, BankAccountNotFoundException {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                return bankAccountMapper.fromSavingAccount((SavingAccount) bankAccount);
            } else {
                return bankAccountMapper.fromCurrentAccount((CurrentAccount) bankAccount);
            }
        }).toList();
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        return bankAccountMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Update customer with ID: {}", customerDTO.getId());
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapper.fromCustomer(savedCustomer);
    }
    
    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Delete customer with ID: {}", customerId);
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account not found");
        }
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(accountOperation -> {
            return bankAccountMapper.fromAccountOperation(accountOperation);
        }).toList();
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account not found");
        }
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(operation -> {
            return bankAccountMapper.fromAccountOperation(operation);
        }).toList();
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(accountId);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.findByNameContains(keyword);
        return customers.stream().map(customer -> {
            return bankAccountMapper.fromCustomer(customer);
        }).toList();
    }

    @Override
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return customer.getBankAccounts()
                .stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof SavingAccount) {
                        return bankAccountMapper.fromSavingAccount((SavingAccount) bankAccount);
                    } else {
                        return bankAccountMapper.fromCurrentAccount((CurrentAccount) bankAccount);
                    }
                })
                .collect(Collectors.toList());
    }


}
