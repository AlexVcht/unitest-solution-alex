package com.kindredgroup.unibetlivetest.repository;

import com.kindredgroup.unibetlivetest.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> getCustomerByPseudo(String pseudo);


    List<Customer> getCustomerByIdIn(List<Long> customerIds);
}
