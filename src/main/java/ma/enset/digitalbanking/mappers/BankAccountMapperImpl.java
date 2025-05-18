package ma.enset.digitalbanking.mappers;

import com.fasterxml.jackson.databind.util.BeanUtil;
import ma.enset.digitalbanking.dtos.CurrentAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.dtos.SavingAccountDTO;
import ma.enset.digitalbanking.entities.CurrentAccount;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {

    public CustomerDTO fromCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerDTO customerDTO = new CustomerDTO();
//        BeanUtils.copyProperties(customer, new CustomerDTO());
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        return customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        Customer customer = new Customer();
//        BeanUtils.copyProperties(new CustomerDTO(), customer);
        customer.setId(customerDTO.getId());
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        return customer;
    }

    public SavingAccountDTO fromSavingAccount(SavingAccount savingAccount) {
        if (savingAccount == null) {
            return null;
        }
        SavingAccountDTO savingAccountDTO = new SavingAccountDTO();
//        BeanUtils.copyProperties(savingAccount, savingAccountDTO);
        savingAccountDTO.setId(savingAccount.getId());
        savingAccountDTO.setBalance(savingAccount.getBalance());
        savingAccountDTO.setCreatedAt(savingAccount.getCreatedAt());
        savingAccountDTO.setStatus(savingAccount.getStatus());
        savingAccountDTO.setInterestRate(savingAccount.getInterestRate());
        savingAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
        savingAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingAccountDTO;
    }

    public SavingAccount fromSavingAccountDTO(SavingAccountDTO savingAccountDTO) {
        if (savingAccountDTO == null) {
            return null;
        }
        SavingAccount savingAccount = new SavingAccount();
//        BeanUtils.copyProperties(savingAccountDTO, savingAccount);
        savingAccount.setId(savingAccountDTO.getId());
        savingAccount.setBalance(savingAccountDTO.getBalance());
        savingAccount.setCreatedAt(savingAccountDTO.getCreatedAt());
        savingAccount.setStatus(savingAccountDTO.getStatus());
        savingAccount.setInterestRate(savingAccountDTO.getInterestRate());
        savingAccount.setCustomer(fromCustomerDTO(savingAccountDTO.getCustomerDTO()));
        return savingAccount;
    }

    public CurrentAccountDTO fromCurrentAccount(CurrentAccount currentAccount) {
        if (currentAccount == null) {
            return null;
        }
        CurrentAccountDTO currentAccountDTO = new CurrentAccountDTO();
//        BeanUtils.copyProperties(currentAccount, currentAccountDTO);
        currentAccountDTO.setId(currentAccount.getId());
        currentAccountDTO.setBalance(currentAccount.getBalance());
        currentAccountDTO.setCreatedAt(currentAccount.getCreatedAt());
        currentAccountDTO.setStatus(currentAccount.getStatus());
        currentAccountDTO.setOverDraft(currentAccount.getOverDraft());
        currentAccountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
        currentAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return currentAccountDTO;
    }

    public CurrentAccount fromCurrentAccountDTO(CurrentAccountDTO currentAccountDTO) {
        if (currentAccountDTO == null) {
            return null;
        }
        CurrentAccount currentAccount = new CurrentAccount();
//        BeanUtils.copyProperties(currentAccountDTO, currentAccount);
        currentAccount.setId(currentAccountDTO.getId());
        currentAccount.setBalance(currentAccountDTO.getBalance());
        currentAccount.setCreatedAt(currentAccountDTO.getCreatedAt());
        currentAccount.setStatus(currentAccountDTO.getStatus());
        currentAccount.setOverDraft(currentAccountDTO.getOverDraft());
        currentAccount.setCustomer(fromCustomerDTO(currentAccountDTO.getCustomerDTO()));
        return currentAccount;
    }
}
