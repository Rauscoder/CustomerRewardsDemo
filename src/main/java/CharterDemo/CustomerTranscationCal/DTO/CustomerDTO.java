package CharterDemo.CustomerTranscationCal.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class CustomerDTO {

    @NotBlank(message = "name can not be null")
    @Size(min = 5, message= "name must be at least 5 characters long")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "name must be a string of letters")
    private String name;

    @NotBlank(message = "transaction id can not be null")
    @Size(min = 8, message= "TransactionId must be at least 8 characters long")
    private String transactionId;

    @NotNull(message="transaction can not be null")
    private Integer transactionInDollar;

    // This handles both INPUT (JSON -> DTO) and OUTPUT (DTO -> JSON) in the given Date Format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @NotNull(message="Date and Time can not be null")
    private LocalDateTime transactionDateAndTime;


}
