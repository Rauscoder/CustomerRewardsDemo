package charterDemo.customerTransaction.service;

import charterDemo.customerTransaction.entity.CustomerTransaction;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import charterDemo.customerTransaction.repository.TransactionRepos;
import charterDemo.customerTransaction.DTO.CustomerRewardDetailsDTO;
import charterDemo.customerTransaction.exception.TransactionNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Slf4j

@Data
public class TransactionService {

    private final TransactionRepos transactionRepos;

    public TransactionService(TransactionRepos transactionRepos){
        this.transactionRepos = transactionRepos;
    }

    public CustomerRewardDetailsDTO getRewardsByCustomer(String customerId,Optional<LocalDateTime> startDate,Optional<LocalDateTime> endDate) {

        if((startDate.isEmpty()&& endDate.isPresent())||(startDate.isPresent()&& endDate.isEmpty())){
            ResponseStatusException ex=new ResponseStatusException(HttpStatus.BAD_REQUEST,"Either one of startDate/endDate is empty which is not a valid request so kindly provide either both startDate and endDate or both empty");
            log.error("Throwing Exception from CustomerTransactionService in getRewardsByCustomer() as either one of startDate/endDate is empty",ex);
            throw ex;
        }
        if(startDate.isPresent()&&endDate.isPresent()&&startDate.get().isAfter(endDate.get())){
            ResponseStatusException ex=new ResponseStatusException(HttpStatus.BAD_REQUEST,"startDate can not be greater then end Date");
            log.error("Throwing Exception from CustomerTransactionService in getRewardsByCustomer() as startDate-{} is greater then endDate-{}",startDate.get(),endDate.get(),ex);
            throw ex;
        }
        List<CustomerTransaction> customerTransactions = null;
        LocalDateTime current=LocalDateTime.now();
        LocalDateTime beforeThreeMonths=current.minusMonths(3);
        CustomerRewardDetailsDTO customerRewardDetailsDTO=null;

        if(startDate.isPresent()&&endDate.isPresent()){
            customerTransactions = transactionRepos.findByCustomerIdAndTransactionDateTimeBetween(customerId,startDate.get(),endDate.get()) ;
            if(customerTransactions.size()>0) {
                customerRewardDetailsDTO = rewardDetails(customerTransactions, startDate.get(), endDate.get());
            }
        }
        else{
            customerTransactions = transactionRepos.findByCustomerIdAndTransactionDateTimeBetween(customerId,LocalDateTime.now().minusMonths(3),LocalDateTime.now()) ;
            if(customerTransactions.size()>0) {
                customerRewardDetailsDTO= rewardDetails(customerTransactions,beforeThreeMonths,current);
            }
        }

        if(customerTransactions.size()==0){
            TransactionNotFoundException ex=new TransactionNotFoundException(customerId+" customerId not found");
            log.error("Throwing Exception from CustomerTransactionService inside getRewardsByCustomer() as {} - customerId not found in db",customerId,ex);
            throw ex;
        }




        log.info("Rewardsdetails-{} of customer with {}-customerId has succesfully evaluated from CustomerTransactionService in getRewardsByCustomer()",customerRewardDetailsDTO,customerId);
        return customerRewardDetailsDTO;
        
    }

    private CustomerRewardDetailsDTO rewardDetails(List<CustomerTransaction> customerTransactions, LocalDateTime lowerDate, LocalDateTime upperDate){
        BigDecimal totalRewards=null;
        BigDecimal rewardPerMonth=null;
        CustomerRewardDetailsDTO customerRewardDetailsDTO=new CustomerRewardDetailsDTO();
        customerRewardDetailsDTO.setCustomerName(customerTransactions.getFirst().getCustomerName());
        customerRewardDetailsDTO.setCustomerTransactionId(customerTransactions.getFirst().getCustomerId());
       // log.info(" current time -{} & time before three months-{}",lowerDate,upperDate);

            totalRewards=calculateRewards(customerTransactions,lowerDate,upperDate);
        System.out.println("********* totalRewards after returning from calculateRewards()---"+totalRewards);
            customerRewardDetailsDTO.setTotalReward(totalRewards);
            LocalDateTime auxilaryLowerDate=lowerDate;
            LocalDateTime auxilaryUpperDate=null;
            List<CustomerTransaction> customerTransactionPerMonth =null;
            Map<String,BigDecimal> rewardDetailsPerMonth=new LinkedHashMap<>();


            while(auxilaryLowerDate.isBefore(upperDate)|| auxilaryLowerDate.isEqual(upperDate)){
                auxilaryUpperDate=auxilaryLowerDate.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
                if(upperDate.isBefore(auxilaryUpperDate)){
                    customerTransactionPerMonth =transactionRepos.findByCustomerIdAndTransactionDateTimeBetween(customerTransactions.getFirst().getCustomerId(),auxilaryLowerDate,upperDate);
                    if(customerTransactionPerMonth.size()>0){
                       rewardPerMonth=calculateRewards(customerTransactionPerMonth,auxilaryLowerDate,upperDate);
                        if(!rewardPerMonth.toString().equals("0.00000")){
                            rewardDetailsPerMonth.put(auxilaryLowerDate.getMonth().toString()+"-"+auxilaryLowerDate.getYear(), rewardPerMonth);
                        }

                    }
                    break;
                }
                else{
                    customerTransactionPerMonth =transactionRepos.findByCustomerIdAndTransactionDateTimeBetween(customerTransactions.getFirst().getCustomerId(),auxilaryLowerDate,auxilaryUpperDate);
                    if(customerTransactionPerMonth.size()>0){
                        rewardPerMonth=calculateRewards(customerTransactionPerMonth,auxilaryLowerDate,auxilaryUpperDate);
                        if(!rewardPerMonth.toString().equals("0.00000")){
                            rewardDetailsPerMonth.put(auxilaryLowerDate.getMonth().toString()+"-"+auxilaryLowerDate.getYear(), rewardPerMonth);
                        }
                    }


                    auxilaryLowerDate=auxilaryLowerDate.plusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);



                }
            }
            customerRewardDetailsDTO.setRewardPerMonth(rewardDetailsPerMonth);
            return customerRewardDetailsDTO;
    }

    private BigDecimal calculateRewards(List<CustomerTransaction> customerTransactions, LocalDateTime lowerDate, LocalDateTime upperDate){
        BigDecimal totalRewards=new BigDecimal(0.0).setScale(5,RoundingMode.DOWN);

        for(int i = 0; i< customerTransactions.size(); i++) {
            if (customerTransactions.get(i).getTransactionDateTime().isEqual(lowerDate) || customerTransactions.get(i).getTransactionDateTime().isEqual(upperDate) || (customerTransactions.get(i).getTransactionDateTime().isBefore(upperDate) && customerTransactions.get(i).getTransactionDateTime().isAfter(lowerDate))) {
                if (customerTransactions.get(i).getTransactionInDollar().doubleValue() > 100) {
                    totalRewards=totalRewards.add(BigDecimal.valueOf((double) 50 + (double) 2 * (customerTransactions.get(i).getTransactionInDollar().doubleValue() - (double) 100))).setScale(5, RoundingMode.DOWN);


                } else if (customerTransactions.get(i).getTransactionInDollar().doubleValue() <= 100 && customerTransactions.get(i).getTransactionInDollar().doubleValue() > 50) {
                    totalRewards=totalRewards.add(BigDecimal.valueOf((double) (customerTransactions.get(i).getTransactionInDollar().doubleValue() - (double) 50))).setScale(5, RoundingMode.DOWN);

                }

            }
        }

            return totalRewards.setScale(5, RoundingMode.DOWN);
    }
}
