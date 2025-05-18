package ma.enset.digitalbanking.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.dtos.AccountHistoryDTO;
import ma.enset.digitalbanking.dtos.AccountOperationDTO;
import ma.enset.digitalbanking.dtos.BankAccountDTO;
import ma.enset.digitalbanking.dtos.CustomerDTO;
import ma.enset.digitalbanking.exceptions.BankAccountNotFoundException;
import ma.enset.digitalbanking.exceptions.CustomerNotFoundException;
import ma.enset.digitalbanking.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class BankAccountRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts")
    public List<BankAccountDTO> listBankAccounts() {
        log.info("Fetching all bank accounts");
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/accounts/{id}")
    public BankAccountDTO getBankAccount(@PathVariable(name = "id") String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

//    @GetMapping("/accounts/{id}/operations")
//    public List<AccountOperationDTO> getHistory(@PathVariable(name = "id") String accountId) throws BankAccountNotFoundException {
//        return bankAccountService.accountHistory(accountId);
//    }

    @GetMapping("/accounts/{id}/operations")
    public AccountHistoryDTO getHistory(@PathVariable(name = "id") String accountId,
                                              @RequestParam (name = "page", defaultValue = "0") int page,
                                              @RequestParam (name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }



}



