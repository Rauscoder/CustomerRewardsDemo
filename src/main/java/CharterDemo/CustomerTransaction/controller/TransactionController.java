package charterDemo.customerTransaction.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import charterDemo.customerTransaction.service.TransactionService;
import charterDemo.customerTransaction.DTO.CustomerRewardDetailsDTO;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
public class TransactionController {

    private  final TransactionService custTransServ;

    public TransactionController(TransactionService custTransServ){
        this.custTransServ=custTransServ;
    }


    @GetMapping ("/getRewardsByCustomer/{customerId}")
    public ResponseEntity<CustomerRewardDetailsDTO> getCustomerRewardsDetails(@PathVariable("customerId") String customerId, @RequestParam(name = "startDate",required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") Optional<LocalDateTime> startDate, @RequestParam(name = "endDate",required = false) @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    Optional<LocalDateTime> endDate){
            log.info("Going into Service From Controller for getting Reward Details of customer for {} -customerId from DB",customerId);
            CustomerRewardDetailsDTO customerRewardDetailsDTO= custTransServ.getRewardsByCustomer(customerId,startDate,endDate);
            log.info("After Getting rewards details of customer for  {} - customerId from DB now Sending response as rewardDetails-{} from controller",customerId,customerRewardDetailsDTO);
            return ResponseEntity.ok(customerRewardDetailsDTO);


    }
}
