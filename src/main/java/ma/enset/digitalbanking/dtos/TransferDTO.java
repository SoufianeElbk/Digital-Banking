package ma.enset.digitalbanking.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    @NotBlank(message = "Source account ID is required")
    private String accountId;  // Changé de accountSource à accountId

    @NotBlank(message = "Destination account ID is required")
    private String accountDestination;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private double amount;

    @NotBlank(message = "Description is required")
    private String description;
}
