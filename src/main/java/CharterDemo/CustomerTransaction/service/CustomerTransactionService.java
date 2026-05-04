package CharterDemo.CustomerTransaction.service;

import CharterDemo.CustomerTransaction.DTO.CustomerCompositeKeyDTO;
import CharterDemo.CustomerTransaction.DTO.CustomerDTO;
import CharterDemo.CustomerTransaction.entity.Customer;
import CharterDemo.CustomerTransaction.keys.CustomerCompositeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import CharterDemo.CustomerTransaction.repository.CustomerRepos;
import CharterDemo.CustomerTransaction.DTO.CustomerRewardDetailsDTO;
import CharterDemo.CustomerTransaction.util.TransactionNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j

@Data
public class CustomerTransactionService {

    private final CustomerRepos customerRepos;

    public CustomerTransactionService(CustomerRepos customerRepos){
        this.customerRepos=customerRepos;
    }


    public void saveTransaction( CustomerDTO customerDTO) {

            Customer customer = new Customer();
            CustomerCompositeKey key = new CustomerCompositeKey(customerDTO.getName(), customerDTO.getTransactionId());
            customer.setId(key);
            customer.setName(customerDTO.getName());
            customer.setTransactionId(customerDTO.getTransactionId());
            customer.setTransactionInDollar(customerDTO.getTransactionInDollar());
            customer.setTransactionDateAndTime(customerDTO.getTransactionDateAndTime());
           // Optional<Customer> customerExists=customerRepos.findById(key);
            boolean present=customerRepos.existsById(key);
            if(present){
                ResponseStatusException ex=new ResponseStatusException(HttpStatus.CONFLICT,"Given Customer Already Existed");
                log.error("Throwing Exception from CustomerTransactionService in saveTransaction() as -{}- customer already exists in db",customer,ex);
                throw ex;
            }
            customerRepos.save(customer);
            log.info("transaction of customer-{} has successfully saved in DB from saveTransaction() of CustomerTransactionService",customer);

    }

    public CustomerRewardDetailsDTO getRewardsByCustomer(String customer) {
        List<Customer> customers=customerRepos.findByName(customer);
        if(customers.size()==0){
            TransactionNotFoundException ex=new TransactionNotFoundException(customer+" customer not found");
            log.error("Throwing Exception from CustomerTransactionService inside getRewardsByCustomer() as {} - name of customer not found in db",customer,ex);
            throw ex;
        }
        LocalDateTime current=LocalDateTime.now();
        LocalDateTime beforeThreeMonths=current.minusMonths(3);
        log.info(" current time -{} & time before three months-{}",current,beforeThreeMonths);
        CustomerRewardDetailsDTO customerRewardDetailsDTO=new CustomerRewardDetailsDTO();
        customerRewardDetailsDTO.setName(customer);
        for(int i=0;i<customers.size();i++){
            if(customers.get(i).getTransactionDateAndTime().isEqual(current)||customers.get(i).getTransactionDateAndTime().isEqual(beforeThreeMonths)||(customers.get(i).getTransactionDateAndTime().isBefore(current)&&customers.get(i).getTransactionDateAndTime().isAfter(beforeThreeMonths))){
                if(customers.get(i).getTransactionInDollar()>100){
                   customerRewardDetailsDTO.setTotalReward((long)50+(long)2*(customers.get(i).getTransactionInDollar()-100));
                }
                else if(customers.get(i).getTransactionInDollar()<=100&&customers.get(i).getTransactionInDollar()>50){
                    customerRewardDetailsDTO.setTotalReward((long)(customers.get(i).getTransactionInDollar()-50));
                }
                else{
                    customerRewardDetailsDTO.setTotalReward((long)0);
                }
            }
        }
        customerRewardDetailsDTO.setRewardPerMonth(customerRewardDetailsDTO.getTotalReward());
        log.info("Rewardsdetails-{} of {}-customer has succesfully evaluated from CustomerTransactionService in getRewardsByCustomer()",customerRewardDetailsDTO,customer);
        return customerRewardDetailsDTO;
        
    }

    public List<CustomerRewardDetailsDTO> getAllCustomerRewardsDetails() {
       Iterable<Customer> allCustomers= customerRepos.findAll();
       SortedSet<String> customerByName=new TreeSet<>();
       List<CustomerRewardDetailsDTO> allCustomerRewardDetailsDTO=new ArrayList<>();
       allCustomers.forEach(customer->{
           customerByName.add(customer.getName());
       });
       if(customerByName.size()==0){
           TransactionNotFoundException ex=new TransactionNotFoundException("transaction Of any customer not found ");
           log.error("Throwing Exception from CustomerTransactionService inside getAllCustomerRewardsDetails() as transaction Of any customer not found inside DB",ex);
           throw ex;

       }
      /* customerByName.forEach(name->{
           allCustomerRewardDetailsDTO.add(getRewardsByCustomer(name));
       });*/
       for(String name:customerByName){
           allCustomerRewardDetailsDTO.add(getRewardsByCustomer(name));
       }
        log.info("Rewardsdetails-{} of each customer has succesfully evaluated from CustomerTransactionService inside getAllCustomerRewardsDetails()",allCustomerRewardDetailsDTO);
       return allCustomerRewardDetailsDTO;
    }

    public void deleteTransaction(CustomerCompositeKeyDTO customerCompositeKeyDTO) {
        CustomerCompositeKey key = new CustomerCompositeKey(customerCompositeKeyDTO.getCustomerName(), customerCompositeKeyDTO.getCustomerTransactionId());
       // Optional<Customer> customer=customerRepos.findById(key);
        boolean present=customerRepos.existsById(key);
        if(present==false){
            TransactionNotFoundException ex=new TransactionNotFoundException(" customer with given transactionId--"+ customerCompositeKeyDTO+" --not found hence can't delete");
            log.error("Throwing Exception from CustomerTransactionService inside deleteTransaction() as  customer with given transactionId--{} not found in db",customerCompositeKeyDTO,ex);
            throw ex;

        }
        customerRepos.deleteById(key);
        log.info("customer with given transactionId--{} has deleted successfully from DB Inside deleteTransaction() of CustomerTransactionService",customerCompositeKeyDTO);
    }
}
