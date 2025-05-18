package ma.enset.digitalbanking.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.enums.AccountStatus;

import java.util.Date;

@Data @AllArgsConstructor @NoArgsConstructor
public class SavingAccountDTO extends BankAccountDTO {
    private double interestRate;
}
