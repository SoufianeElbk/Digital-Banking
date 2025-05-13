package ma.enset.digitalbanking.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class SavingAccount extends BankAccount{
    private double interestRate;
}
