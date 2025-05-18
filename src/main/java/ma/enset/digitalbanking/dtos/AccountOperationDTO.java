package ma.enset.digitalbanking.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.entities.BankAccount;
import ma.enset.digitalbanking.enums.OperationType;

import java.util.Date;

@Data @AllArgsConstructor
@NoArgsConstructor
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}
