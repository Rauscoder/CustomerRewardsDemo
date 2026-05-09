package charterDemo.customerTransaction.repository;

import charterDemo.customerTransaction.entity.CustomerTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepos extends CrudRepository<CustomerTransaction, Long> {
    List<CustomerTransaction> findByCustomerIdAndTransactionDateTimeBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);

}
