package charterDemo.customerTransaction.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JSONErrorResponseDTO {
  @NotNull(message="errorResponse can not be null")
  private Map<String,String> errorResponse;
}
