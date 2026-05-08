package charterDemo.customerTransaction.DTO;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardDetailsDTO {

    @NotBlank(message = "name can not be null")
    @Size(min = 3, message= "name must be at least 3 characters long")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "name must be a string of letters")
    private String customerName;

    @NotBlank(message = "customerTransactionId can not be null")
    @Size(min = 5, message= "customerTransactionId must be at least 5 characters long")
    private String customerTransactionId;

    @NotNull(message="rewardPerMonth can not be null")
    private Map<String,BigDecimal> rewardPerMonth;   //for calculating Reward per month

    @NotNull(message="totalReward can not be null")
    private BigDecimal totalReward;       //for calculating total Reward during 3 month period

}
