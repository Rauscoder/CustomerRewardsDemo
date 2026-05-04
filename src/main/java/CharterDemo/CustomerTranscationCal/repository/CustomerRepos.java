package CharterDemo.CustomerTranscationCal.repository;

import CharterDemo.CustomerTranscationCal.entity.Customer;
import CharterDemo.CustomerTranscationCal.keys.CustomerCompositeKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepos extends CrudRepository<Customer, CustomerCompositeKey> {
    List<Customer> findByName(String name);

}
