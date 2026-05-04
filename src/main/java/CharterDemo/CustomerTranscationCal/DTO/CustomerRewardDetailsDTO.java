package CharterDemo.CustomerTranscationCal.DTO;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardDetailsDTO {

    @NotBlank(message = "name can not be null")
    @Size(min = 5, message= "name must be at least 5 characters long")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "name must be a string of letters")
    private String name;

    @NotNull(message="rewardPerMonth can not be null")
    private Double rewardPerMonth;   //for calculating Reward per month

    private Long totalReward=(long)0;       //for calculating total Reward during 3 month period

    public Double getRewardPerMonth() {
        String result=String.format("%.3f",rewardPerMonth);
        return Double.parseDouble(result);
    }

    public void setRewardPerMonth(Long totalReward) {
        rewardPerMonth = (totalReward/3.0);
    }

    public Long getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(Long totalReward) {
        this.totalReward +=totalReward;
    }


}
