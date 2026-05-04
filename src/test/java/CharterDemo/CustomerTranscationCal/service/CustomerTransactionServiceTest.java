package CharterDemo.CustomerTranscationCal.service;

import CharterDemo.CustomerTranscationCal.DTO.CustomerCompositeKeyDTO;
import CharterDemo.CustomerTranscationCal.DTO.CustomerDTO;
import CharterDemo.CustomerTranscationCal.DTO.CustomerRewardDetailsDTO;
import CharterDemo.CustomerTranscationCal.entity.Customer;
import CharterDemo.CustomerTranscationCal.keys.CustomerCompositeKey;
import CharterDemo.CustomerTranscationCal.repository.CustomerRepos;
import static org.mockito.Mockito.*;

import CharterDemo.CustomerTranscationCal.util.TransactionNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.CsvSources;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerTransactionServiceTest {

    @Mock
    private  CustomerRepos customerRepos;

    @InjectMocks
    private CustomerTransactionService customerTransactionService;



    @ParameterizedTest()
    @MethodSource("provideCustomerDTOForTesting")
    public void testSaveTransactionWithoutException(CustomerDTO customerDTO){

       when(customerRepos.existsById(any(CustomerCompositeKey.class))).thenReturn(false);
       customerTransactionService.saveTransaction(customerDTO);
       verify(customerRepos,times(1)).save(any(Customer.class));
       System.out.println("************----Test for  saving transaction for Customer-----"+customerDTO+" has passed successfully---********");
    }

    @ParameterizedTest()
    @MethodSource("provideCustomerDTOForTesting")
    public void testSaveTransactionForShowingException(CustomerDTO customerDTO){

        when(customerRepos.existsById(any(CustomerCompositeKey.class))).thenReturn(true);

        assertThrows(ResponseStatusException.class,()->{
            customerTransactionService.saveTransaction(customerDTO);
        });
        System.out.println("********---Test for  saving transaction when transaction already exists has thrown exception successfully--*******");
    }

    @ParameterizedTest()
    @MethodSource("provideSameCustomerDTOForGetRewardsTesting")
    public void testGetRewardsByCustomerWithoutException(List<Customer> customers){
        CustomerRewardDetailsDTO exceptedRewardsDetails=calculateRewardsDetailsByCustomerForTest(customers);
        when(customerRepos.findByName(anyString())).thenReturn(customers);
        assertEquals(customerTransactionService.getRewardsByCustomer(customers.getFirst().getName()),exceptedRewardsDetails);
        System.out.println("********---Test for  calculating Rewards Details of given customer has passed successfully--*******");
    }

    @ParameterizedTest()
    @CsvSource({"John Doe","John Baskin","Raushan Kumar"})
    public void testGetRewardsByCustomerForShowingException(String name){
        when(customerRepos.findByName(anyString())).thenReturn(new ArrayList<Customer>());
        assertThrows(TransactionNotFoundException.class,()-> {
            customerTransactionService.getRewardsByCustomer(name);
        });
       System.out.println("********---Test for GettingRewardsDetailsByCustomer  when customer not found has thrown exception successfully--*******");
    }

    @ParameterizedTest()
    @MethodSource("provideListOfCustomersByNameForTesting")
    public void testGetAllCustomerRewardsDetailsWithoutException(List<List<Customer>> allCustomers){
        AtomicInteger index=new AtomicInteger(0);
        List<CustomerRewardDetailsDTO> expectedRewardsAllCustomer=new ArrayList<>();
        List<Customer> flatCustomerList=new ArrayList<>();
        List<List<Customer>> mutableCustomers = new ArrayList<>(allCustomers);

        allCustomers.forEach((customers)->{
            CustomerRewardDetailsDTO exceptedRewardsDetails=calculateRewardsDetailsByCustomerForTest(customers);
            expectedRewardsAllCustomer.add(exceptedRewardsDetails);
            flatCustomerList.addAll(customers);
        });
        expectedRewardsAllCustomer.sort((rewards1,rewards2)->{
            return rewards1.getName().compareTo(rewards2.getName());
        });
        mutableCustomers.sort((customerListByName1,customerListByName2)->{
            return customerListByName1.getFirst().getName().compareTo(customerListByName2.getFirst().getName());
        });

        when(customerRepos.findAll()).thenReturn(flatCustomerList);
        when(customerRepos.findByName(anyString())).thenAnswer(invocation->{
                    return mutableCustomers.get(index.getAndIncrement());
        });

        assertEquals(customerTransactionService.getAllCustomerRewardsDetails(),expectedRewardsAllCustomer);
        System.out.println("********---Test for  calculating Rewards Details of all customer has passed successfully--*******");

    }

    @Test
    public void testGetAllCustomerRewardsDetailsForShowingException(){
        when(customerRepos.findAll()).thenReturn(new ArrayList<Customer>());

        assertThrows(TransactionNotFoundException.class,()->{
            customerTransactionService.getAllCustomerRewardsDetails();
        });
        System.out.println("********---Test for when no customer is present in DB has thrown exception successfully--*******");
    }

    @ParameterizedTest
    @CsvSource({"John baskin,TXN-452389","John Doe,TXN-345619"})
    public void testDeleteTransactionWithoutException(String name,String transactionId){
        CustomerCompositeKeyDTO customerCompositeKeyDTO=new CustomerCompositeKeyDTO(name,transactionId);
        when(customerRepos.existsById(any(CustomerCompositeKey.class))).thenReturn(true);
        doNothing().when(customerRepos).deleteById(any(CustomerCompositeKey.class));
        customerTransactionService.deleteTransaction(customerCompositeKeyDTO);
        verify(customerRepos,times(1)).deleteById(any(CustomerCompositeKey.class));

        System.out.println("********---Test for delete transaction for customer with given transactionId has passed successfully--*******");
    }


    @ParameterizedTest
    @CsvSource({"John baskin,TXN-452389","John Doe,TXN-345619"})
    public void testDeleteTransactionForShowingException(String name,String transactionId){
        CustomerCompositeKeyDTO customerCompositeKeyDTO=new CustomerCompositeKeyDTO(name,transactionId);
        when(customerRepos.existsById(any(CustomerCompositeKey.class))).thenReturn(false);

        assertThrows(TransactionNotFoundException.class,()->{
            customerTransactionService.deleteTransaction(customerCompositeKeyDTO);
        });
        System.out.println("********---Test for delete transaction when no used found with transaction has thrown exception successfully--*******");
    }


















    /*******************************************************************************************************************************/

    private static Stream<CustomerDTO> provideCustomerDTOForTesting(){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return Stream.of(new CustomerDTO("John Doe","TXN-847675",751,LocalDateTime.parse("24-03-2025 20:30:15",formatter))
                ,new CustomerDTO("John Doe","TXN-947675",51,LocalDateTime.parse("24-03-2026 20:30:15",formatter))
                ,new CustomerDTO("John Doe","TXN-947655",351,LocalDateTime.parse("25-03-2026 20:30:15",formatter))
                ,new CustomerDTO("John Baskin","TXN-947655",351,LocalDateTime.parse("25-04-2026 20:30:15",formatter))
                ,new CustomerDTO("John Baskin","TXN-9476547",51,LocalDateTime.parse("03-03-2026 10:30:15",formatter)));
    }

    public static Stream<List<List<Customer>>> provideListOfCustomersByNameForTesting() {
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
       return  Stream.of(List.of(List.of(new Customer(null,"John Doe","TXN-847675",751,LocalDateTime.parse("24-03-2025 20:30:15",formatter))
                ,new Customer(null,"John Doe","TXN-947675",51,LocalDateTime.parse("24-03-2026 20:30:15",formatter))
                ,new Customer(null,"John Doe","TXN-947655",351,LocalDateTime.parse("25-03-2026 20:30:15",formatter)))
                ,List.of(new Customer(null,"John Baskin","TXN-947655",351,LocalDateTime.parse("25-04-2026 20:30:15",formatter))
                ,new Customer(null,"John Baskin","TXN-9476547",51,LocalDateTime.parse("03-03-2026 10:30:15",formatter))
                ,new Customer(null,"John Baskin","TXN-94765478",151,LocalDateTime.parse("04-02-2026 20:30:15",formatter))
                ,new Customer(null,"John Baskin","TXN-94765478",60,LocalDateTime.parse("04-02-2026 10:30:15",formatter)))
        ));
    }


    private static Stream<List<Customer>> provideSameCustomerDTOForGetRewardsTesting(){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return Stream.of(List.of(new Customer(null,"John Doe","TXN-847675",751,LocalDateTime.parse("24-03-2025 20:30:15",formatter))
                ,new Customer(null,"John Doe","TXN-947675",51,LocalDateTime.parse("24-03-2026 20:30:15",formatter))
                ,new Customer(null,"John Doe","TXN-947655",351,LocalDateTime.parse("25-03-2026 20:30:15",formatter))));
    }

    private static CustomerRewardDetailsDTO calculateRewardsDetailsByCustomerForTest(List<Customer> customers){
        LocalDateTime current=LocalDateTime.now();
        LocalDateTime beforeThreeMonths=current.minusMonths(3);
        //log.info(" current time -{} & time before three months-{}",current,beforeThreeMonths);
        System.out.println("current time-"+current+"-time before three months-"+beforeThreeMonths);
        CustomerRewardDetailsDTO customerRewardDetailsDTO=new CustomerRewardDetailsDTO();
        customerRewardDetailsDTO.setName(customers.get(0).getName());
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
        return customerRewardDetailsDTO;
    }

}