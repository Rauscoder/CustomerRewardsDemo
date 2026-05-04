package CharterDemo.CustomerTransaction.repository;

import CharterDemo.CustomerTransaction.entity.Customer;
import CharterDemo.CustomerTransaction.keys.CustomerCompositeKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepos extends CrudRepository<Customer, CustomerCompositeKey> {
    List<Customer> findByName(String name);

}
