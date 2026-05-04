package CharterDemo.CustomerTransaction.controller;

import CharterDemo.CustomerTransaction.DTO.CustomerCompositeKeyDTO;
import CharterDemo.CustomerTransaction.DTO.CustomerDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import CharterDemo.CustomerTransaction.service.CustomerTransactionService;
import CharterDemo.CustomerTransaction.DTO.CustomerRewardDetailsDTO;

import java.util.List;
@Slf4j
@RestController
public class CustomerTransactionController {

    private  final CustomerTransactionService custTransServ;

    public CustomerTransactionController(CustomerTransactionService custTransServ){
        this.custTransServ=custTransServ;
    }

    @PostMapping("/createtransaction")
    public ResponseEntity<String> createTransaction(@Valid @RequestBody CustomerDTO customerDTO){
            log.info("{} - customer with transaction Going into Service From Controller for saving into DB",customerDTO);
            custTransServ.saveTransaction(customerDTO);
            log.info("After Saving {} -customer with given transaction successfully in DB now Sending response from controller",customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("transaction of "+customerDTO.getName()+" Customer Succesfully saved");

    }

    @DeleteMapping("/deleteTransaction")
    public ResponseEntity<String> deletetransaction(@Valid @RequestBody CustomerCompositeKeyDTO customerCompositeKeyDTO)
    {
            log.info("Going into Service From Controller for deleting transactionId {} of {} customer from DB",customerCompositeKeyDTO.getCustomerTransactionId(),customerCompositeKeyDTO.getCustomerName());
            custTransServ.deleteTransaction(customerCompositeKeyDTO);
            log.info("After deleting -{}- customer for -{}- transactionId successfully in DB now Sending response from controller",customerCompositeKeyDTO.getCustomerName(),customerCompositeKeyDTO.getCustomerTransactionId());
            return ResponseEntity.status(HttpStatus.OK).body("transaction of  customer for given transactionId Deleted");


    }

    @GetMapping ("/getRewardsByCustomer/{name}")
    public ResponseEntity<CustomerRewardDetailsDTO> getCustomerRewardsDetails(@PathVariable("name") String customer){
            log.info("Going into Service From Controller for getting Reward Details for {} -customer from DB",customer);
            CustomerRewardDetailsDTO customerRewardDetailsDTO= custTransServ.getRewardsByCustomer(customer);
            log.info("After Getting rewards details of {} - customer from DB now Sending response as rewardDetails-{} from controller",customer,customerRewardDetailsDTO);
            return ResponseEntity.ok(customerRewardDetailsDTO);


    }

    @GetMapping ("/getRewardsForAllCustomers")
    public ResponseEntity<List<CustomerRewardDetailsDTO>> getAllCustomerRewardsDetails(){
            log.info("Going into Service From Controller for getting Reward Details for Each customer from DB");
            List<CustomerRewardDetailsDTO> rewardDetails= custTransServ.getAllCustomerRewardsDetails();
            log.info("After Getting rewards details of Each customer from DB now Sending response as {} from controller",rewardDetails);
            return ResponseEntity.ok(rewardDetails);


    }





}
