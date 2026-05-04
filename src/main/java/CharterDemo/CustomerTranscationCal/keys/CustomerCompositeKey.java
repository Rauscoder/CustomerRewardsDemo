package CharterDemo.CustomerTranscationCal.keys;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerCompositeKey {

    private String customerName;
    private String customerTransactionId;
}
