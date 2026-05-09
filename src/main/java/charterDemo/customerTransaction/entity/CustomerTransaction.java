package charterDemo.customerTransaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class CustomerTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

   @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false,unique = true)
    private String transactionId;

    @Column(nullable = false, precision = 19, scale = 5)
    private BigDecimal transactionInDollar;

    @Column(nullable = false)
    private LocalDateTime transactionDateTime;

}
