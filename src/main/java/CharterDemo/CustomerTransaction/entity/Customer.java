package CharterDemo.CustomerTransaction.entity;

import jakarta.persistence.*;
import CharterDemo.CustomerTransaction.keys.CustomerCompositeKey;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {


   @EmbeddedId
    private CustomerCompositeKey id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Integer transactionInDollar;

    @Column(nullable = false)
    private LocalDateTime transactionDateAndTime;



}
