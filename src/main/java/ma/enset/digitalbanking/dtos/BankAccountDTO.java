package ma.enset.digitalbanking.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.digitalbanking.entities.AccountOperation;
import ma.enset.digitalbanking.entities.Customer;
import ma.enset.digitalbanking.enums.AccountStatus;

import java.util.Date;
import java.util.List;


@Data @AllArgsConstructor @NoArgsConstructor
public class BankAccountDTO {

    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private String type;
}
