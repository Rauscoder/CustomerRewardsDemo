package CharterDemo.CustomerTranscationCal.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerCompositeKeyDTO {

    @NotBlank(message = "customerName can not be null")
    @Size(min = 5, message= "customerName must be at least 5 characters long")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "customerName must be a string of letters")
    private String customerName;

    @NotBlank(message = "transaction id can not be null")
    @Size(min = 8, message= "TransactionId must be at least 8 characters long")
    private String customerTransactionId;
}
