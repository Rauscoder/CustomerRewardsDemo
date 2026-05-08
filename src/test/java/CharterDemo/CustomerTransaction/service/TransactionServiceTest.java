package charterDemo.customerTransaction.service;

import charterDemo.customerTransaction.DTO.CustomerRewardDetailsDTO;
import charterDemo.customerTransaction.entity.CustomerTransaction;
import charterDemo.customerTransaction.repository.TransactionRepos;
import static org.mockito.Mockito.*;

import charterDemo.customerTransaction.exception.TransactionNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepos transactionRepos;

    @InjectMocks
    private TransactionService transactionService;





    @ParameterizedTest()
    @MethodSource("provideCustomerTransactionForTesting")

    public void testGetRewardsByCustomerWithoutException(List<CustomerTransaction> transactions, String customerId,
                                                         Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate){
        LocalDateTime current=LocalDateTime.now();
        LocalDateTime beforeThreeMonths=LocalDateTime.now().minusMonths(3);
        List<CustomerTransaction> customerTransactions=null;
        CustomerRewardDetailsDTO  expectedCustomerRewardDetailsDTO=null;
        if(startDate.isPresent()&&endDate.isPresent()){
            customerTransactions = findByCustomerIdAndTransactionDateTimeBetween(transactions,customerId,startDate.get(),endDate.get()) ;

            expectedCustomerRewardDetailsDTO = rewardDetails(customerTransactions, startDate.get(), endDate.get());

        }
        else{
            customerTransactions = findByCustomerIdAndTransactionDateTimeBetween(transactions,customerId,LocalDateTime.now().minusMonths(3),LocalDateTime.now()) ;

            expectedCustomerRewardDetailsDTO= rewardDetails(customerTransactions,beforeThreeMonths,current);

        }
        System.out.println("customerRewardsDetailsExcepted--"+expectedCustomerRewardDetailsDTO);
        when(transactionRepos.findByCustomerIdAndTransactionDateTimeBetween(anyString(),any(LocalDateTime.class),any(LocalDateTime.class)))
                .thenAnswer(invocation->{
                    String customerid=invocation.getArgument(0);
                    LocalDateTime lowerdate=invocation.getArgument(1);
                    LocalDateTime upperdate=invocation.getArgument(2);
                  List<CustomerTransaction>  customertransactions = findByCustomerIdAndTransactionDateTimeBetween(transactions,customerid,lowerdate,upperdate) ;
                  return customertransactions;
                });
        assertEquals(transactionService.getRewardsByCustomer(transactions.getFirst().getCustomerId(),startDate,endDate),expectedCustomerRewardDetailsDTO);
       

    }

    @ParameterizedTest()
    @MethodSource("provideArgumentsForOneDateNullTesting")
    public void testGetRewardsByCustomerForShowingExceptionWhenOneDateNull(String customerId,Optional<LocalDateTime> startdate,Optional<LocalDateTime> enddate){
      //  when(transactionRepos.findByName(anyString())).thenReturn(new ArrayList<Transaction>());
        assertThrows(ResponseStatusException.class,()-> {
            transactionService.getRewardsByCustomer(customerId, startdate,enddate);
        });

    }

    @ParameterizedTest()
    @MethodSource("provideArgumentsForStartDateGreaterTesting")
    public void testGetRewardsByCustomerForShowingExceptionWhenStartDateGreater(String customerId,Optional<LocalDateTime> startdate,Optional<LocalDateTime> enddate){

        assertThrows(ResponseStatusException.class,()-> {
            transactionService.getRewardsByCustomer(customerId, startdate,enddate);
        });

    }

    @ParameterizedTest()
    @CsvSource({"John2345","dummy2356","Baskin4567"})
    public void testGetRewardsByCustomerForShowingTransactionNotFoundException(String customerId){
        LocalDateTime dummyStartDate=LocalDateTime.MIN;
        LocalDateTime dummyEndDate=LocalDateTime.MAX;

        when(transactionRepos.findByCustomerIdAndTransactionDateTimeBetween(anyString(),any(LocalDateTime.class),any(LocalDateTime.class))).thenReturn(new ArrayList<CustomerTransaction>());
        assertThrows(TransactionNotFoundException.class,()-> {
            transactionService.getRewardsByCustomer(customerId, Optional.of(dummyStartDate),Optional.of(dummyEndDate));
        });
        assertThrows(TransactionNotFoundException.class,()-> {
            transactionService.getRewardsByCustomer(customerId, Optional.empty(),Optional.empty());
        });


    }


    private List<CustomerTransaction> findByCustomerIdAndTransactionDateTimeBetween(List<CustomerTransaction> customerTransactions,String customerId,LocalDateTime lowerDate,LocalDateTime upperDate){
        List<CustomerTransaction> transactionsWithinGivenDates=new ArrayList<>();
        for(int i = 0; i< customerTransactions.size(); i++) {
            if(!customerTransactions.getFirst().getCustomerId().equals(customerId)){
                break;
            }
            if (customerTransactions.get(i).getTransactionDateTime().isEqual(lowerDate) || customerTransactions.get(i).getTransactionDateTime().isEqual(upperDate) || (customerTransactions.get(i).getTransactionDateTime().isBefore(upperDate)
                    && customerTransactions.get(i).getTransactionDateTime().isAfter(lowerDate))) {
                transactionsWithinGivenDates.add(customerTransactions.get(i));
            }

        }
         return transactionsWithinGivenDates;
    }

    private CustomerRewardDetailsDTO rewardDetails(List<CustomerTransaction> customerTransactions, LocalDateTime lowerDate, LocalDateTime upperDate){
        BigDecimal totalRewards=null;
        BigDecimal rewardPerMonth=null;
        CustomerRewardDetailsDTO customerRewardDetailsDTO=new CustomerRewardDetailsDTO();
        customerRewardDetailsDTO.setCustomerName(customerTransactions.getFirst().getCustomerName());
        customerRewardDetailsDTO.setCustomerTransactionId(customerTransactions.getFirst().getCustomerId());


        totalRewards=calculateRewards(customerTransactions,lowerDate,upperDate);

        customerRewardDetailsDTO.setTotalReward(totalRewards);
        LocalDateTime auxilaryLowerDate=lowerDate;
        LocalDateTime auxilaryUpperDate=null;
        List<CustomerTransaction> customerTransactionPerMonth =null;
        Map<String,BigDecimal> rewardDetailsPerMonth=new LinkedHashMap<>();


        while(auxilaryLowerDate.isBefore(upperDate)|| auxilaryLowerDate.isEqual(upperDate)){
            auxilaryUpperDate=auxilaryLowerDate.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
            if(upperDate.isBefore(auxilaryUpperDate)){
                customerTransactionPerMonth =findByCustomerIdAndTransactionDateTimeBetween(customerTransactions,customerTransactions.getFirst().getCustomerId(),auxilaryLowerDate,upperDate);
                if(customerTransactionPerMonth.size()>0){
                    rewardPerMonth=calculateRewards(customerTransactionPerMonth,auxilaryLowerDate,upperDate);
                    if(!rewardPerMonth.toString().equals("0.00000")){
                        rewardDetailsPerMonth.put(auxilaryLowerDate.getMonth().toString()+"-"+auxilaryLowerDate.getYear(), rewardPerMonth);
                    }

                }
                break;
            }
            else{
                customerTransactionPerMonth =findByCustomerIdAndTransactionDateTimeBetween(customerTransactions,customerTransactions.getFirst().getCustomerId(),auxilaryLowerDate,auxilaryUpperDate);
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
            if (customerTransactions.get(i).getTransactionDateTime().isEqual(lowerDate) || customerTransactions.get(i).getTransactionDateTime().isEqual(upperDate) || (customerTransactions.get(i).getTransactionDateTime().isBefore(upperDate)
                    && customerTransactions.get(i).getTransactionDateTime().isAfter(lowerDate))) {
                if (customerTransactions.get(i).getTransactionInDollar().doubleValue() > 100) {
                    totalRewards=totalRewards.add(BigDecimal.valueOf((double) 50 + (double) 2 * (customerTransactions.get(i).getTransactionInDollar().doubleValue() - (double) 100))).setScale(5, RoundingMode.DOWN);


                } else if (customerTransactions.get(i).getTransactionInDollar().doubleValue() <= 100 && customerTransactions.get(i).getTransactionInDollar().doubleValue() > 50) {
                    totalRewards=totalRewards.add(BigDecimal.valueOf((double) (customerTransactions.get(i).getTransactionInDollar().doubleValue() - (double) 50))).setScale(5, RoundingMode.DOWN);

                }

            }
        }

        return totalRewards.setScale(5, RoundingMode.DOWN);
    }

    private static Stream<Arguments> provideCustomerTransactionForTesting() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


        List<CustomerTransaction> johnTransactions = List.of(
                new CustomerTransaction(null, "John1234", "John Doe", "TXN-847675", new BigDecimal("151.00000"), LocalDateTime.parse("01-01-2026 20:30:15", formatter)),
                new CustomerTransaction(null, "John1234", "John Doe", "TXN-947675", new BigDecimal("51.00000"), LocalDateTime.parse("26-01-2026 10:30:15", formatter)),
                new CustomerTransaction(null, "John1234", "John Doe", "TXN-947655", new BigDecimal("151.00000"), LocalDateTime.parse("01-02-2026 00:00:01", formatter)),
                new CustomerTransaction(null, "John1234", "John Doe", "TXN-8476753", new BigDecimal("51.00000"), LocalDateTime.parse("28-02-2026 23:59:59", formatter)),
                new CustomerTransaction(null, "John1234", "John Doe", "TXN-9476752", new BigDecimal("151.00000"), LocalDateTime.parse("01-03-2026 00:00:05", formatter)),
                new CustomerTransaction(null, "John1234", "John Doe", "TXN-9476551", new BigDecimal("51.00000"), LocalDateTime.parse("25-03-2026 12:30:15", formatter))
        );

        List<CustomerTransaction> john5Transactions = List.of(
                new CustomerTransaction(null, "John12345", "John Doe", "TXN-9476551", new BigDecimal("151.00000"), LocalDateTime.parse("25-03-2026 12:30:15", formatter))
        );

        List<CustomerTransaction> baskinTransactions = List.of(
                new CustomerTransaction(null, "Baskin1234", "John Baskin", "TXN1-947654", new BigDecimal("151.00000"), LocalDateTime.parse("01-04-2025 22:30:46", formatter)),
                new CustomerTransaction(null, "Baskin1234", "John Baskin", "TXN-9476547", new BigDecimal("151.00000"), LocalDateTime.parse("09-07-2025 19:30:15", formatter)),
                new CustomerTransaction(null, "Baskin1234", "John Baskin", "TXN1-9476547", new BigDecimal("51.00000"), LocalDateTime.parse("03-01-2026 10:30:15", formatter))
        );

        return Stream.of(

                Arguments.of(
                        johnTransactions,
                        "John1234",
                        Optional.of(LocalDateTime.parse("01-01-2026 00:00:00", formatter)),
                        Optional.of(LocalDateTime.parse("31-03-2026 23:59:59", formatter))
                ),


                Arguments.of(
                        john5Transactions,
                        "John12345",
                        Optional.empty(),
                        Optional.empty()
                ),


                Arguments.of(
                        baskinTransactions,
                        "Baskin1234",
                        Optional.of(LocalDateTime.parse("01-04-2025 00:00:00", formatter)),
                        Optional.of(LocalDateTime.parse("31-03-2026 23:59:59", formatter))
                )
        );
    }


    private static Stream<Arguments> provideArgumentsForOneDateNullTesting(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return Stream.of(

                Arguments.of(

                        "John1234",
                        Optional.empty(),
                        Optional.of(LocalDateTime.parse("31-03-2026 23:59:59", formatter))
                ),


                Arguments.of(

                        "John12345",
                        Optional.of(LocalDateTime.parse("31-03-2026 23:59:59", formatter)),
                        Optional.empty()
                ),


                Arguments.of(

                        "Baskin1234",
                        Optional.of(LocalDateTime.parse("01-04-2025 00:00:00", formatter)),
                        Optional.empty()
                ) );
    }

    private static Stream<Arguments> provideArgumentsForStartDateGreaterTesting(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return Stream.of(

                Arguments.of(

                        "John1234",
                        Optional.of(LocalDateTime.parse("31-03-2026 23:59:59", formatter)),
                        Optional.of(LocalDateTime.parse("01-01-2026 00:00:00", formatter))


                ),


                Arguments.of(

                        "John12345",
                        Optional.of(LocalDateTime.parse("31-05-2026 23:59:59", formatter)),
                        Optional.of(LocalDateTime.parse("01-04-2026 00:00:00", formatter))
                ),


                Arguments.of(

                        "Baskin1234",
                        Optional.of(LocalDateTime.parse("31-03-2026 23:59:59", formatter)),
                        Optional.of(LocalDateTime.parse("01-04-2025 00:00:00", formatter))
                ) );
    }


}
